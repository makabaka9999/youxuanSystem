package com.youxuan.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.dto.AfterSaleCreateRequest;
import com.youxuan.order.model.AfterSaleDO;
import com.youxuan.order.model.OrderDO;
import com.youxuan.order.model.OrderItemDO;
import com.youxuan.order.repository.AfterSaleRepository;
import com.youxuan.order.repository.OrderItemRepository;
import com.youxuan.order.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 售后业务服务
 * <p>
 * 提供售后单的创建、查询、商家审核及平台介入等全流程管理功能。
 * 支持仅退款（REFUND_ONLY）和退货退款（RETURN_REFUND）两种售后类型。
 * 售后单流转路径：
 * <ul>
 *   <li>仅退款：APPLYING -> MERCHANT_APPROVED -> COMPLETED</li>
 *   <li>退货退款：APPLYING -> MERCHANT_APPROVED -> USER_RETURNED -> COMPLETED</li>
 *   <li>拒绝：APPLYING -> MERCHANT_REJECTED -> CLOSED</li>
 *   <li>平台介入：任意状态 -> PLATFORM_INTERVENING -> COMPLETED/CLOSED</li>
 * </ul>
 * </p>
 */
@Service
public class AfterSaleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterSaleService.class);

    @Autowired
    private AfterSaleRepository afterSaleRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建售后申请
     * <p>
     * 用户对已支付的订单发起售后申请。校验订单归属和状态后创建售后单。
     * </p>
     *
     * @param userId  用户ID
     * @param request 售后申请参数
     * @return 创建的售后单
     */
    @Transactional(rollbackFor = Exception.class)
    public AfterSaleDO createAfterSale(Long userId, AfterSaleCreateRequest request) {
        // 查询订单
        OrderDO order = orderRepository.findById(request.getOrderId());
        if (order == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此订单");
        }
        // 只有已支付、已发货、已完成状态的订单可申请售后
        if (!OrderStatusConstants.ORDER_PAID.equals(order.getOrderStatus())
                && !OrderStatusConstants.ORDER_SHIPPED.equals(order.getOrderStatus())
                && !OrderStatusConstants.ORDER_COMPLETED.equals(order.getOrderStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "当前订单状态不允许申请售后");
        }

        // 校验订单项（如果指定了 orderItemId）
        if (request.getOrderItemId() != null) {
            OrderItemDO item = orderItemRepository.findById(request.getOrderItemId());
            if (item == null || !item.getOrderId().equals(order.getId())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "订单项不存在");
            }
        }

        // 校验售后类型
        if (!OrderStatusConstants.AFTER_SALE_REFUND_ONLY.equals(request.getType())
                && !OrderStatusConstants.AFTER_SALE_RETURN_REFUND.equals(request.getType())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "不支持的售后类型");
        }

        // 校验退款金额
        if (request.getApplyAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "退款金额必须大于0");
        }
        if (request.getApplyAmount().compareTo(order.getPayableAmount()) > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "退款金额不能超过订单金额");
        }

        // 序列化凭证图片
        String evidenceUrls = null;
        if (request.getEvidenceUrls() != null && !request.getEvidenceUrls().isEmpty()) {
            try {
                evidenceUrls = objectMapper.writeValueAsString(request.getEvidenceUrls());
            } catch (JsonProcessingException e) {
                throw new BizException(ErrorCode.INTERNAL_ERROR, "凭证图片序列化失败");
            }
        }

        // 创建售后单
        AfterSaleDO afterSale = new AfterSaleDO();
        afterSale.setId(idGenerator.nextId());
        afterSale.setAfterSaleNo(generateAfterSaleNo());
        afterSale.setOrderId(order.getId());
        afterSale.setOrderItemId(request.getOrderItemId());
        afterSale.setUserId(userId);
        afterSale.setMerchantId(order.getMerchantId());
        afterSale.setType(request.getType());
        afterSale.setStatus(OrderStatusConstants.AFTER_SALE_APPLYING);
        afterSale.setApplyAmount(request.getApplyAmount());
        afterSale.setApprovedAmount(BigDecimal.ZERO);
        afterSale.setReason(request.getReason());
        afterSale.setDescription(request.getDescription());
        afterSale.setEvidenceUrls(evidenceUrls);

        afterSaleRepository.insert(afterSale);

        LOGGER.info("售后申请已创建. afterSaleNo={}, orderId={}, type={}, amount={}",
                afterSale.getAfterSaleNo(), request.getOrderId(), request.getType(), request.getApplyAmount());

        return afterSale;
    }

    /**
     * 分页查询用户的售后单
     *
     * @param userId   用户ID
     * @param pageNo   页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<AfterSaleDO> getAfterSales(Long userId, int pageNo, int pageSize) {
        List<AfterSaleDO> list = afterSaleRepository.findByUserId(userId, pageNo, pageSize);
        int total = afterSaleRepository.countByUserId(userId);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 获取售后单详情
     *
     * @param id 售后单ID
     * @return 售后单对象
     */
    public AfterSaleDO getAfterSaleDetail(Long id) {
        AfterSaleDO afterSale = afterSaleRepository.findById(id);
        if (afterSale == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "售后单不存在");
        }
        return afterSale;
    }

    /**
     * 分页查询商家的售后单
     *
     * @param merchantId 商家ID
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @return 分页结果
     */
    public PageResponse<AfterSaleDO> getMerchantAfterSales(Long merchantId, int pageNo, int pageSize) {
        List<AfterSaleDO> list = afterSaleRepository.findByMerchantId(merchantId, pageNo, pageSize);
        int total = afterSaleRepository.countByMerchantId(merchantId);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 商家处理售后申请
     * <p>
     * 商家对用户提交的售后申请进行审核，可选择同意（APPROVE）或拒绝（REJECT）。
     * </p>
     *
     * @param merchantId 商家ID
     * @param afterSaleId 售后单ID
     * @param action     审核动作：APPROVE-同意，REJECT-拒绝
     * @param amount     同意退款金额（仅 APPROVE 时有效）
     * @param reason     审核意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void handleMerchantDecision(Long merchantId, Long afterSaleId, String action,
                                        BigDecimal amount, String reason) {
        AfterSaleDO afterSale = afterSaleRepository.findById(afterSaleId);
        if (afterSale == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "售后单不存在");
        }
        if (!afterSale.getMerchantId().equals(merchantId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此售后单");
        }
        if (!OrderStatusConstants.AFTER_SALE_APPLYING.equals(afterSale.getStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "当前售后状态不允许操作");
        }

        if ("APPROVE".equals(action)) {
            // 校验退款金额
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "退款金额必须大于0");
            }
            if (amount.compareTo(afterSale.getApplyAmount()) > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "同意退款金额不能超过申请金额");
            }

            afterSale.setStatus(OrderStatusConstants.AFTER_SALE_MERCHANT_APPROVED);
            afterSale.setApprovedAmount(amount);
            afterSale.setMerchantReason(reason);

            // 如果是仅退款，直接完成；退货退款需要用户退货
            if (OrderStatusConstants.AFTER_SALE_REFUND_ONLY.equals(afterSale.getType())) {
                afterSale.setStatus(OrderStatusConstants.AFTER_SALE_COMPLETED);
            }
        } else if ("REJECT".equals(action)) {
            afterSale.setStatus(OrderStatusConstants.AFTER_SALE_MERCHANT_REJECTED);
            afterSale.setMerchantReason(reason);
        } else {
            throw new BizException(ErrorCode.PARAM_INVALID, "不支持的审核动作");
        }

        afterSaleRepository.updateStatus(afterSale);
        LOGGER.info("商家处理售后. afterSaleId={}, action={}, amount={}", afterSaleId, action, amount);
    }

    /**
     * 平台介入处理售后
     * <p>
     * 平台客服对商家和用户无法达成一致的售后单进行最终仲裁。
     * 平台决定后可终结售后单（COMPLETED 或 CLOSED）。
     * </p>
     *
     * @param afterSaleId 售后单ID
     * @param decision    平台决定：APPROVE-同意退款，REJECT-拒绝退款
     * @param amount      最终退款金额
     * @param reason      平台处理意见
     */
    @Transactional(rollbackFor = Exception.class)
    public void platformIntervene(Long afterSaleId, String decision, BigDecimal amount, String reason) {
        AfterSaleDO afterSale = afterSaleRepository.findById(afterSaleId);
        if (afterSale == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "售后单不存在");
        }

        // 更新售后单状态为平台介入中
        afterSale.setStatus(OrderStatusConstants.AFTER_SALE_PLATFORM_INTERVENING);
        afterSale.setPlatformReason(reason);
        afterSaleRepository.updateStatus(afterSale);

        if ("APPROVE".equals(decision)) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "退款金额必须大于0");
            }
            afterSale.setStatus(OrderStatusConstants.AFTER_SALE_COMPLETED);
            afterSale.setApprovedAmount(amount);
        } else if ("REJECT".equals(decision)) {
            afterSale.setStatus(OrderStatusConstants.AFTER_SALE_CLOSED);
        } else {
            throw new BizException(ErrorCode.PARAM_INVALID, "不支持的决定");
        }

        afterSale.setPlatformReason(reason);
        afterSaleRepository.updateStatus(afterSale);
        LOGGER.info("平台介入处理售后. afterSaleId={}, decision={}, amount={}", afterSaleId, decision, amount);
    }

    /**
     * 提交退货物流信息
     * <p>
     * 用户在商家同意退货退款后填写退货运单信息，系统更新售后单状态为 USER_RETURNED。
     * P0 阶段物流信息存储在售后单的 remark 字段中，不创建独立的 return_shipments 记录。
     * </p>
     *
     * @param userId          用户ID
     * @param afterSaleId     售后单ID
     * @param logisticsCompany 物流公司
     * @param trackingNo      运单号
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitReturnShipment(Long userId, Long afterSaleId, String logisticsCompany, String trackingNo) {
        AfterSaleDO afterSale = afterSaleRepository.findById(afterSaleId);
        if (afterSale == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "售后单不存在");
        }
        if (!afterSale.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此售后单");
        }
        if (!OrderStatusConstants.AFTER_SALE_MERCHANT_APPROVED.equals(afterSale.getStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "当前售后状态不允许提交退货物流");
        }
        if (!OrderStatusConstants.AFTER_SALE_RETURN_REFUND.equals(afterSale.getType())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "仅退货退款类型需要提交物流信息");
        }

        afterSale.setStatus(OrderStatusConstants.AFTER_SALE_USER_RETURNED);
        afterSale.setRemark("退货物流: " + logisticsCompany + " - " + trackingNo);
        afterSaleRepository.updateStatus(afterSale);

        LOGGER.info("退货物流已提交. afterSaleId={}, logisticsCompany={}, trackingNo={}",
                afterSaleId, logisticsCompany, trackingNo);
    }

    /**
     * 生成售后单号
     *
     * @return 售后单号
     */
    private String generateAfterSaleNo() {
        return "AFS" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
