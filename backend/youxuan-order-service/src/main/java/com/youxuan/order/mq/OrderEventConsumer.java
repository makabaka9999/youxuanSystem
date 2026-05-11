package com.youxuan.order.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 订单事件消费者
 * <p>
 * 监听订单和支付领域的业务消息队列，处理异步消息。
 * 当前阶段以日志记录为主，记录收到的事件详情。
 * 后续阶段将扩展为真实的业务处理，如：
 * - 订单已支付：通知库存服务正式扣减库存
 * - 订单已取消：通知库存服务回退库存
 * - 订单已创建：触发超时未支付自动取消定时器
 * </p>
 */
@Component
public class OrderEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 解析 JSON 格式的消息为 Map，便于提取字段
     *
     * @param message 原始消息体（JSON 字符串）
     * @return 解析后的键值对，解析失败时返回 null
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseMessage(String message) {
        try {
            return objectMapper.readValue(message, Map.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("解析事件消息 JSON 失败. message={}", message, e);
            return null;
        }
    }

    /**
     * 处理订单创建事件
     * <p>
     * 订单创建成功后触发，后续可用于：
     * - 发送订单创建通知给用户
     * - 触发超时未支付自动取消定时器
     * - 记录订单创建埋点数据
     * </p>
     *
     * @param message JSON 格式的事件消息
     * @param channel RabbitMQ 信道（用于手动确认）
     * @param msg     Spring AMQP 消息对象（可获取投递标签等元数据）
     */
    @RabbitListener(queues = "${youxuan.mq.queue.order.created}")
    public void handleOrderCreated(String message, Channel channel, Message msg) {
        LOGGER.info("收到订单创建事件: {}", message);
        Map<String, Object> data = parseMessage(message);
        if (data != null) {
            String eventId = (String) data.get("eventId");
            String orderNo = (String) data.get("orderNo");
            String orderId = (String) data.get("orderId");
            LOGGER.info("处理订单创建事件: eventId={}, orderId={}, orderNo={}", eventId, orderId, orderNo);
            // TODO: 后续实现——发送订单创建通知、触发超时取消定时器等
            LOGGER.info("订单创建事件处理完成（待扩展）. orderNo={}", orderNo);
        }
        // 手动确认消息，确保消费完成后才从队列移除
        acknowledgeMessage(channel, msg);
    }

    /**
     * 处理订单支付成功事件
     * <p>
     * 订单支付成功后触发，后续可用于：
     * - 通知库存服务正式扣减库存
     * - 发送支付成功通知给用户
     * - 触发商家接单提醒
     * </p>
     *
     * @param message JSON 格式的事件消息
     * @param channel RabbitMQ 信道
     * @param msg     Spring AMQP 消息对象
     */
    @RabbitListener(queues = "${youxuan.mq.queue.order.paid}")
    public void handleOrderPaid(String message, Channel channel, Message msg) {
        LOGGER.info("收到订单支付成功事件: {}", message);
        Map<String, Object> data = parseMessage(message);
        if (data != null) {
            String eventId = (String) data.get("eventId");
            String orderNo = (String) data.get("orderNo");
            LOGGER.info("处理订单支付成功事件: eventId={}, orderNo={}", eventId, orderNo);
            // TODO: 后续实现——库存正式扣减、发送通知等
            LOGGER.info("订单支付成功事件处理完成（待扩展）. orderNo={}", orderNo);
        }
        acknowledgeMessage(channel, msg);
    }

    /**
     * 处理订单发货事件
     * <p>
     * 商家发货后触发，后续可用于：
     * - 发送物流通知给用户
     * - 更新订单物流状态
     * </p>
     *
     * @param message JSON 格式的事件消息
     * @param channel RabbitMQ 信道
     * @param msg     Spring AMQP 消息对象
     */
    @RabbitListener(queues = "${youxuan.mq.queue.order.shipped}")
    public void handleOrderShipped(String message, Channel channel, Message msg) {
        LOGGER.info("收到订单发货事件: {}", message);
        Map<String, Object> data = parseMessage(message);
        if (data != null) {
            String eventId = (String) data.get("eventId");
            String orderNo = (String) data.get("orderNo");
            String logisticsCompany = (String) data.get("logisticsCompany");
            String trackingNo = (String) data.get("trackingNo");
            LOGGER.info("处理订单发货事件: eventId={}, orderNo={}, logisticsCompany={}, trackingNo={}",
                    eventId, orderNo, logisticsCompany, trackingNo);
            // TODO: 后续实现——发送物流通知等
            LOGGER.info("订单发货事件处理完成（待扩展）. orderNo={}", orderNo);
        }
        acknowledgeMessage(channel, msg);
    }

    /**
     * 处理订单取消事件
     * <p>
     * 订单取消后触发，后续可用于：
     * - 通知库存服务回退已占用的库存
     * - 释放已使用的优惠券
     * - 发送取消通知给用户
     * </p>
     *
     * @param message JSON 格式的事件消息
     * @param channel RabbitMQ 信道
     * @param msg     Spring AMQP 消息对象
     */
    @RabbitListener(queues = "${youxuan.mq.queue.order.canceled}")
    public void handleOrderCanceled(String message, Channel channel, Message msg) {
        LOGGER.info("收到订单取消事件: {}", message);
        Map<String, Object> data = parseMessage(message);
        if (data != null) {
            String eventId = (String) data.get("eventId");
            String orderNo = (String) data.get("orderNo");
            LOGGER.info("处理订单取消事件: eventId={}, orderNo={}", eventId, orderNo);
            // TODO: 后续实现——库存回退、优惠券释放等
            LOGGER.info("订单取消事件处理完成（待扩展）. orderNo={}", orderNo);
        }
        acknowledgeMessage(channel, msg);
    }

    /**
     * 手动确认消息
     * <p>
     * application.yml 中配置了 acknowledge-mode: manual（手动确认模式），
     * 消费者处理完业务逻辑后必须调用 basicAck 确认消息，否则消息会一直留在队列中。
     * </p>
     *
     * @param channel RabbitMQ 信道
     * @param msg     Spring AMQP 消息对象（包含投递标签）
     */
    private void acknowledgeMessage(Channel channel, Message msg) {
        try {
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            LOGGER.error("确认消息失败. deliveryTag={}", msg.getMessageProperties().getDeliveryTag(), e);
        }
    }
}
