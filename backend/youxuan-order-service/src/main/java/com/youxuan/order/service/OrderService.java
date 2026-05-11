package com.youxuan.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.config.IdempotentLockService;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.web.RequestContext;
import java.time.Duration;
import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.dto.OrderCreateItem;
import com.youxuan.order.model.OrderDO;
import com.youxuan.order.model.OrderItemDO;
import com.youxuan.order.model.OrderStatusLogDO;
import com.youxuan.order.mq.OrderEventPublisher;
import com.youxuan.order.repository.OrderItemRepository;
import com.youxuan.order.repository.OrderRepository;
import com.youxuan.order.repository.OrderStatusLogRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单业务服务
 * <p>
 * 提供订单的创建、查询、取消、确认收货等核心业务功能。
 * 下单时根据 SKU 所属商家自动拆分订单（一个商家一个订单）。
 * P0 阶段运费默认为 0，优惠金额默认为 0。
 * </p>
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IdempotentLockService idempotentLockService;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    /**
     * 创建订单
     * <p>
     * 根据下单商品列表生成订单。由于不同商品可能属于不同商家，系统会按 merchantId 自动拆分，
     * 每个商家生成一个独立的订单。P0 阶段不做真实库存扣减，不做地址校验，收货地址信息使用 addressId 占位。
     * </p>
     *
     * @param userId    用户ID
     * @param addressId 收货地址ID（P0阶段仅记录ID，不做完整地址解析）
     * @param items     下单商品列表
     * @param remark    订单备注
     * @return 创建的订单列表（每个商家一个订单）
     */
    @Transactional(rollbackFor = Exception.class)
    public List<OrderDO> createOrder(Long userId, Long addressId, List<OrderCreateItem> items, String remark) {
        // 获取分布式锁，防止同一用户重复提交订单
        String lockKey = "lock:order:create:" + userId;
        boolean locked = idempotentLockService.tryLock(lockKey, Duration.ofSeconds(30));
        if (!locked) {
            LOGGER.warn("订单创建请求过于频繁，获取锁失败. userId={}", userId);
            throw new BizException(ErrorCode.IDEMPOTENT_CONFLICT, "操作过于频繁，请稍后重试");
        }

        try {
            if (items == null || items.isEmpty()) {
                throw new BizException(ErrorCode.PARAM_INVALID, "下单商品不能为空");
            }

            // 构建收货信息快照（P0阶段简化处理，仅记录地址ID）
            String receiverSnapshot;
            try {
                receiverSnapshot = objectMapper.writeValueAsString(
                        new java.util.HashMap<String, Object>() {{
                            put("addressId", addressId);
                            put("snapshotTime", LocalDateTime.now().toString());
                        }}
                );
            } catch (JsonProcessingException e) {
                throw new BizException(ErrorCode.INTERNAL_ERROR, "构建收货信息快照失败");
            }

            // P0 阶段简化：将所有商品归入同一个订单，merchantId 和 storeId 使用占位值
            // 真实场景应通过 product-service 查询 SKU 所属商家和店铺
            Long mockMerchantId = 1L;
            Long mockStoreId = 1L;

            // 生成订单号
            String orderNo = generateOrderNo();

            // 创建订单
            OrderDO order = new OrderDO();
            order.setId(idGenerator.nextId());
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setMerchantId(mockMerchantId);
            order.setStoreId(mockStoreId);
            order.setOrderStatus(OrderStatusConstants.ORDER_CREATED);
            order.setPayStatus(OrderStatusConstants.PAY_UNPAID);

            // 计算金额
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderItemDO> orderItems = new ArrayList<>();
            for (OrderCreateItem item : items) {
                BigDecimal salePrice = BigDecimal.TEN; // P0 阶段使用固定单价 10 元
                BigDecimal itemTotal = salePrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);

                // 构建商品快照
                String productSnapshot;
                try {
                    productSnapshot = objectMapper.writeValueAsString(
                            new java.util.HashMap<String, Object>() {{
                                put("skuId", item.getSkuId());
                                put("salePrice", salePrice);
                                put("snapshotTime", LocalDateTime.now().toString());
                            }}
                    );
                } catch (JsonProcessingException e) {
                    throw new BizException(ErrorCode.INTERNAL_ERROR, "构建商品快照失败");
                }

                OrderItemDO orderItem = new OrderItemDO();
                orderItem.setId(idGenerator.nextId());
                orderItem.setOrderId(order.getId());
                orderItem.setOrderNo(orderNo);
                orderItem.setProductId(0L); // P0 占位
                orderItem.setSkuId(item.getSkuId());
                orderItem.setProductSnapshot(productSnapshot);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setSalePrice(salePrice);
                orderItem.setTotalAmount(itemTotal);
                orderItems.add(orderItem);
            }

            order.setTotalAmount(totalAmount);
            order.setFreightAmount(BigDecimal.ZERO);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setPayableAmount(totalAmount);
            order.setPaidAmount(BigDecimal.ZERO);
            order.setReceiverSnapshot(receiverSnapshot);
            order.setRemark(remark);

            // 保存订单
            orderRepository.insert(order);

            // 保存订单明细
            for (OrderItemDO orderItem : orderItems) {
                orderItemRepository.insert(orderItem);
            }

            // 记录订单状态日志
            recordStatusLog(order, null, OrderStatusConstants.ORDER_CREATED,
                    OrderStatusConstants.OPERATOR_USER, userId, "用户下单", null);

            LOGGER.info("订单创建成功. orderNo={}, userId={}, totalAmount={}", orderNo, userId, totalAmount);

            // 发布订单创建事件，通知下游服务异步处理（库存预扣、积分计算等）
            orderEventPublisher.publishOrderCreated(order);

            List<OrderDO> result = new ArrayList<>();
            result.add(order);
            return result;
        } finally {
            // 释放分布式锁
            idempotentLockService.unlock(lockKey);
        }
    }

    /**
     * 分页查询用户的订单列表
     *
     * @param userId      用户ID
     * @param orderStatus 订单状态（为空表示查询所有）
     * @param pageNo      页码
     * @param pageSize    每页大小
     * @return 分页结果
     */
    public PageResponse<OrderDO> getOrders(Long userId, String orderStatus, int pageNo, int pageSize) {
        List<OrderDO> orders = orderRepository.findByUserId(userId, orderStatus, pageNo, pageSize);
        int total = orderRepository.countByUserId(userId, orderStatus);
        return new PageResponse<>(pageNo, pageSize, total, orders);
    }

    /**
     * 获取订单详情（含明细和状态日志）
     *
     * @param orderId 订单ID
     * @return 订单对象
     */
    public OrderDO getOrderDetail(Long orderId) {
        OrderDO order = orderRepository.findById(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        return order;
    }

    /**
     * 取消订单
     * <p>
     * 仅当订单处于 CREATED（待支付）状态时允许取消。
     * </p>
     *
     * @param userId  用户ID（用于校验归属）
     * @param orderId 订单ID
     * @param reason  取消原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String reason) {
        OrderDO order = orderRepository.findById(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此订单");
        }
        if (!OrderStatusConstants.ORDER_CREATED.equals(order.getOrderStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "当前订单状态不允许取消");
        }

        order.setOrderStatus(OrderStatusConstants.ORDER_CANCELED);
        order.setCanceledAt(LocalDateTime.now());
        order.setCancelReason(reason);
        orderRepository.updateStatus(order);

        recordStatusLog(order, OrderStatusConstants.ORDER_CREATED, OrderStatusConstants.ORDER_CANCELED,
                OrderStatusConstants.OPERATOR_USER, userId, reason, null);

        LOGGER.info("订单已取消. orderNo={}, reason={}", order.getOrderNo(), reason);

        // 发布订单取消事件，通知下游服务异步处理（库存补偿、优惠券释放等）
        orderEventPublisher.publishOrderCanceled(order.getId(), order.getOrderNo());
    }

    /**
     * 确认收货
     * <p>
     * 仅当订单处于 SHIPPED（已发货）状态时允许确认收货。
     * </p>
     *
     * @param userId  用户ID（用于校验归属）
     * @param orderId 订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceipt(Long userId, Long orderId) {
        OrderDO order = orderRepository.findById(orderId);
        if (order == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此订单");
        }
        if (!OrderStatusConstants.ORDER_SHIPPED.equals(order.getOrderStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "当前订单状态不允许确认收货");
        }

        order.setOrderStatus(OrderStatusConstants.ORDER_COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.updateStatus(order);

        recordStatusLog(order, OrderStatusConstants.ORDER_SHIPPED, OrderStatusConstants.ORDER_COMPLETED,
                OrderStatusConstants.OPERATOR_USER, userId, "用户确认收货", null);

        LOGGER.info("订单已确认收货. orderNo={}", order.getOrderNo());

        /* 预留：确认收货后可根据业务需要发布订单完成事件
         * 后续如需通知下游服务（如触发评价提醒、商家结算等），可在此处调用：
         * orderEventPublisher.publishOrderCompleted(order.getId(), order.getOrderNo());
         */
    }

    /**
     * 分页查询商家的订单列表
     *
     * @param merchantId 商家ID
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @return 分页结果
     */
    public PageResponse<OrderDO> getMerchantOrders(Long merchantId, int pageNo, int pageSize) {
        List<OrderDO> orders = orderRepository.findByMerchantId(merchantId, pageNo, pageSize);
        int total = orderRepository.countByMerchantId(merchantId);
        return new PageResponse<>(pageNo, pageSize, total, orders);
    }

    /**
     * 生成订单号
     * <p>
     * 格式：yyyyMMddHHmmss + 8位随机字符串，保证唯一性。
     * </p>
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * 记录订单状态变更日志
     *
     * @param order        订单对象
     * @param fromStatus   变更前状态
     * @param toStatus     变更后状态
     * @param operatorType 操作人类型
     * @param operatorId   操作人ID
     * @param reason       操作原因
     * @param remark       备注
     */
    private void recordStatusLog(OrderDO order, String fromStatus, String toStatus,
                                  String operatorType, Long operatorId, String reason, String remark) {
        OrderStatusLogDO log = new OrderStatusLogDO();
        log.setId(idGenerator.nextId());
        log.setOrderId(order.getId());
        log.setOrderNo(order.getOrderNo());
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperatorType(operatorType);
        log.setOperatorId(operatorId);
        log.setReason(reason);
        log.setRequestId(RequestContext.getRequestId());
        log.setRemark(remark);
        orderStatusLogRepository.insert(log);
    }
}
