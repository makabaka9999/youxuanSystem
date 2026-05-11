package com.youxuan.order.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.model.OrderDO;
import com.youxuan.order.model.OrderStatusLogDO;
import com.youxuan.order.model.PaymentOrderDO;
import com.youxuan.order.repository.OrderRepository;
import com.youxuan.order.repository.OrderStatusLogRepository;
import com.youxuan.order.repository.PaymentOrderRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付业务服务
 * <p>
 * P0 阶段提供支付单的创建和模拟支付回调处理功能，不对接真实第三方支付渠道。
 * 创建支付单后，可通过 {@link #handlePaymentCallback} 模拟支付成功回调来推进订单状态。
 * </p>
 */
@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusLogRepository orderStatusLogRepository;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 创建支付单
     * <p>
     * 根据订单号创建一笔支付记录。一个订单只能创建一笔支付（已存在支付单时返回已存在的记录）。
     * P0 阶段支付创建后不会真实发起渠道支付，状态停留在 INIT。
     * </p>
     *
     * @param userId  用户ID
     * @param orderNo 订单号
     * @param channel 支付渠道
     * @return 支付单对象
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentOrderDO createPayment(Long userId, String orderNo, String channel) {
        // 查询订单
        OrderDO order = orderRepository.findByOrderNo(orderNo);
        if (order == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作此订单");
        }
        if (!OrderStatusConstants.ORDER_CREATED.equals(order.getOrderStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "订单状态不允许支付");
        }

        // 检查是否已有支付单
        PaymentOrderDO existing = paymentOrderRepository.findByOrderId(order.getId());
        if (existing != null) {
            LOGGER.warn("订单已有支付单，返回已存在的支付单. orderNo={}, paymentNo={}", orderNo, existing.getPaymentNo());
            return existing;
        }

        // 创建支付单
        PaymentOrderDO payment = new PaymentOrderDO();
        payment.setId(idGenerator.nextId());
        payment.setPaymentNo(generatePaymentNo());
        payment.setOrderId(order.getId());
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setChannel(channel);
        payment.setPayAmount(order.getPayableAmount());
        payment.setPayStatus(OrderStatusConstants.PAYMENT_INIT);
        payment.setRemark("");

        paymentOrderRepository.insert(payment);
        LOGGER.info("支付单创建成功. paymentNo={}, orderNo={}, amount={}", payment.getPaymentNo(), orderNo, order.getPayableAmount());

        return payment;
    }

    /**
     * 处理支付回调
     * <p>
     * P0 阶段的模拟支付回调处理。根据渠道和回调内容模拟支付成功/失败，并更新订单状态。
     * 真实场景下需要对回调签名进行验签、防重放等安全校验。
     * </p>
     *
     * @param channel 支付渠道
     * @param payload 回调数据（P0 阶段可传入 "SUCCESS" 或 "FAILED" 模拟结果）
     */
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentCallback(String channel, String payload) {
        LOGGER.info("收到支付回调. channel={}, payload={}", channel, payload);

        // P0 阶段简化解析：从 payload 中提取 paymentNo 和结果
        // 真实场景需从第三方回调数据中解析
        String paymentNo = payload; // 简化处理
        String result = "SUCCESS";   // 默认成功

        PaymentOrderDO payment = paymentOrderRepository.findByPaymentNo(paymentNo);
        if (payment == null) {
            LOGGER.error("支付回调中支付单不存在. paymentNo={}", paymentNo);
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "支付单不存在");
        }

        if (!OrderStatusConstants.PAYMENT_INIT.equals(payment.getPayStatus())
                && !OrderStatusConstants.PAYMENT_PAYING.equals(payment.getPayStatus())) {
            LOGGER.warn("支付单状态不允许处理回调. paymentNo={}, status={}", paymentNo, payment.getPayStatus());
            return;
        }

        if ("SUCCESS".equals(result)) {
            // 更新支付单状态为成功
            payment.setPayStatus(OrderStatusConstants.PAYMENT_SUCCESS);
            payment.setThirdTradeNo("MOCK_" + UUID.randomUUID().toString().substring(0, 16));
            payment.setPaidAt(LocalDateTime.now());
            payment.setCallbackPayload("{\"mock\":true,\"result\":\"SUCCESS\"}");
            paymentOrderRepository.updateStatus(payment);

            // 更新订单状态为已支付
            OrderDO order = orderRepository.findById(payment.getOrderId());
            if (order != null && OrderStatusConstants.ORDER_CREATED.equals(order.getOrderStatus())) {
                order.setOrderStatus(OrderStatusConstants.ORDER_PAID);
                order.setPayStatus(OrderStatusConstants.PAY_PAID);
                order.setPaidAmount(payment.getPayAmount());
                order.setPaidAt(LocalDateTime.now());
                orderRepository.updateStatus(order);

                // 记录状态日志
                OrderStatusLogDO log = new OrderStatusLogDO();
                log.setId(idGenerator.nextId());
                log.setOrderId(order.getId());
                log.setOrderNo(order.getOrderNo());
                log.setFromStatus(OrderStatusConstants.ORDER_CREATED);
                log.setToStatus(OrderStatusConstants.ORDER_PAID);
                log.setOperatorType(OrderStatusConstants.OPERATOR_SYSTEM);
                log.setReason("支付回调处理成功");
                log.setRequestId(RequestContext.getRequestId());
                orderStatusLogRepository.insert(log);

                LOGGER.info("订单支付成功. orderNo={}, paymentNo={}", order.getOrderNo(), paymentNo);
            }
        } else {
            // 更新支付单状态为失败
            payment.setPayStatus(OrderStatusConstants.PAYMENT_FAILED);
            payment.setCallbackPayload("{\"mock\":true,\"result\":\"FAILED\"}");
            paymentOrderRepository.updateStatus(payment);
            LOGGER.warn("订单支付失败. paymentNo={}", paymentNo);
        }
    }

    /**
     * 生成支付单号
     *
     * @return 支付单号
     */
    private String generatePaymentNo() {
        return "PAY" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
