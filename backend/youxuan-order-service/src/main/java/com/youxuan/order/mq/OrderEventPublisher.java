package com.youxuan.order.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.constant.MqConstants;
import com.youxuan.common.exception.BizException;
import com.youxuan.order.model.OrderDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 订单事件发布器
 * <p>
 * 负责将订单领域的业务事件发布到 RabbitMQ 消息队列中，
 * 包括：订单创建、支付成功、发货、取消等事件。
 * 下游服务（如商品服务、库存服务、商家服务等）通过监听这些事件
 * 进行库存扣减、积分计算、物流通知等异步处理。
 * </p>
 */
@Service
public class OrderEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventPublisher.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 构建事件消息的公共负载
     * <p>
     * 每个事件消息包含：事件唯一ID、事件类型、发生时间戳，以及业务相关的数据字段。
     * 统一的结构便于消费者进行通用处理和日志追踪。
     * </p>
     *
     * @param eventType 事件类型（如 ORDER_CREATED、ORDER_PAID 等）
     * @param data      业务数据键值对
     * @return JSON 字符串格式的事件消息
     */
    private String buildEventMessage(String eventType, Map<String, Object> data) {
        Map<String, Object> message = new HashMap<>();
        message.put("eventId", UUID.randomUUID().toString().replace("-", ""));
        message.put("eventType", eventType);
        message.put("timestamp", LocalDateTime.now().toString());
        if (data != null) {
            message.putAll(data);
        }
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("序列化事件消息失败. eventType={}", eventType, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "序列化事件消息失败");
        }
    }

    /**
     * 发布订单创建事件
     * <p>
     * 订单创建成功后调用，通知下游服务执行库存预扣、优惠券占用等异步操作。
     * 消息投递到订单交换机，路由键为 order.created。
     * </p>
     *
     * @param order 已创建成功的订单对象，从中提取订单ID、订单号、金额等关键信息
     */
    public void publishOrderCreated(OrderDO order) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(order.getId()));
        data.put("orderNo", order.getOrderNo());
        data.put("userId", String.valueOf(order.getUserId()));
        data.put("merchantId", String.valueOf(order.getMerchantId()));
        data.put("storeId", String.valueOf(order.getStoreId()));
        data.put("totalAmount", order.getTotalAmount() != null ? order.getTotalAmount().toPlainString() : "0");
        data.put("payableAmount", order.getPayableAmount() != null ? order.getPayableAmount().toPlainString() : "0");

        String message = buildEventMessage("ORDER_CREATED", data);
        rabbitTemplate.convertAndSend(MqConstants.ORDER_EXCHANGE, "order.created", message);
        LOGGER.info("订单创建事件已发布. orderNo={}, eventType=ORDER_CREATED", order.getOrderNo());
    }

    /**
     * 发布订单支付成功事件
     * <p>
     * 支付回调处理成功后调用，通知下游服务执行库存正式扣减、订单超时取消任务关闭等操作。
     * 消息投递到订单交换机，路由键为 order.paid。
     * </p>
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     */
    public void publishOrderPaid(Long orderId, String orderNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);

        String message = buildEventMessage("ORDER_PAID", data);
        rabbitTemplate.convertAndSend(MqConstants.ORDER_EXCHANGE, "order.paid", message);
        LOGGER.info("订单支付成功事件已发布. orderNo={}, eventType=ORDER_PAID", orderNo);
    }

    /**
     * 发布订单发货事件
     * <p>
     * 商家执行发货操作后调用，通知下游服务推送物流状态给用户等。
     * 消息投递到订单交换机，路由键为 order.shipped。
     * </p>
     *
     * @param orderId          订单ID
     * @param orderNo          订单号
     * @param logisticsCompany 物流公司名称（如 顺丰速运、中通快递）
     * @param trackingNo       物流单号
     */
    public void publishOrderShipped(Long orderId, String orderNo, String logisticsCompany, String trackingNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);
        data.put("logisticsCompany", logisticsCompany);
        data.put("trackingNo", trackingNo);

        String message = buildEventMessage("ORDER_SHIPPED", data);
        rabbitTemplate.convertAndSend(MqConstants.ORDER_EXCHANGE, "order.shipped", message);
        LOGGER.info("订单发货事件已发布. orderNo={}, logisticsCompany={}, trackingNo={}, eventType=ORDER_SHIPPED",
                orderNo, logisticsCompany, trackingNo);
    }

    /**
     * 发布订单取消事件
     * <p>
     * 订单取消成功后调用，通知下游服务进行库存补偿（回退库存）、释放优惠券等操作。
     * 消息投递到订单交换机，路由键为 order.canceled。
     * </p>
     *
     * @param orderId 订单ID
     * @param orderNo 订单号
     */
    public void publishOrderCanceled(Long orderId, String orderNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);

        String message = buildEventMessage("ORDER_CANCELED", data);
        rabbitTemplate.convertAndSend(MqConstants.ORDER_EXCHANGE, "order.canceled", message);
        LOGGER.info("订单取消事件已发布. orderNo={}, eventType=ORDER_CANCELED", orderNo);
    }

    /**
     * 发布支付创建事件
     * <p>
     * 创建支付单成功后调用，通知下游服务记录支付流水等。
     * 消息投递到支付交换机，路由键为 payment.created。
     * </p>
     *
     * @param orderId   订单ID
     * @param orderNo   订单号
     * @param paymentNo 支付单号
     */
    public void publishPaymentCreated(Long orderId, String orderNo, String paymentNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);
        data.put("paymentNo", paymentNo);

        String message = buildEventMessage("PAYMENT_CREATED", data);
        rabbitTemplate.convertAndSend(MqConstants.PAYMENT_EXCHANGE, "payment.created", message);
        LOGGER.info("支付创建事件已发布. paymentNo={}, orderNo={}, eventType=PAYMENT_CREATED", paymentNo, orderNo);
    }

    /**
     * 发布支付成功事件
     * <p>
     * 支付回调处理成功后调用，通知下游服务更新订单支付状态、触发后续业务流程等。
     * 消息投递到支付交换机，路由键为 payment.success。
     * </p>
     *
     * @param orderId   订单ID
     * @param orderNo   订单号
     * @param paymentNo 支付单号
     */
    public void publishPaymentSuccess(Long orderId, String orderNo, String paymentNo) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);
        data.put("paymentNo", paymentNo);

        String message = buildEventMessage("PAYMENT_SUCCESS", data);
        rabbitTemplate.convertAndSend(MqConstants.PAYMENT_EXCHANGE, "payment.success", message);
        LOGGER.info("支付成功事件已发布. paymentNo={}, orderNo={}, eventType=PAYMENT_SUCCESS", paymentNo, orderNo);
    }

    /**
     * 发布支付失败事件
     * <p>
     * 支付回调处理失败后调用，通知下游服务记录失败原因、触发告警等。
     * 消息投递到支付交换机，路由键为 payment.failed。
     * </p>
     *
     * @param orderId   订单ID
     * @param orderNo   订单号
     * @param paymentNo 支付单号
     * @param reason    失败原因描述
     */
    public void publishPaymentFailed(Long orderId, String orderNo, String paymentNo, String reason) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", String.valueOf(orderId));
        data.put("orderNo", orderNo);
        data.put("paymentNo", paymentNo);
        data.put("reason", reason);

        String message = buildEventMessage("PAYMENT_FAILED", data);
        rabbitTemplate.convertAndSend(MqConstants.PAYMENT_EXCHANGE, "payment.failed", message);
        LOGGER.info("支付失败事件已发布. paymentNo={}, orderNo={}, reason={}, eventType=PAYMENT_FAILED",
                paymentNo, orderNo, reason);
    }
}
