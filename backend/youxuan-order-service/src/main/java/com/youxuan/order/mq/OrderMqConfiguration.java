package com.youxuan.order.mq;

import com.youxuan.common.constant.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单模块 RabbitMQ 队列绑定配置
 * <p>
 * 声明订单模块专属的消息队列及其与交换机的绑定关系。
 * 交换机已在 youxuan-common 模块的 {@link com.youxuan.common.config.RabbitMqAutoConfiguration} 中统一声明，
 * 本配置仅补充本模块所需的业务队列（订单创建、支付、发货、取消等）和支付事件队列。
 * 队列名称通过 application.yml 中的配置项注入，便于根据不同环境调整。
 * </p>
 */
@Configuration
public class OrderMqConfiguration {

    // ==================== 订单事件队列 ====================

    /**
     * 订单创建事件队列
     * <p>接收订单创建成功后的通知消息。</p>
     */
    @Bean
    public Queue orderCreatedQueue(@Value("${youxuan.mq.queue.order.created}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * 订单支付成功事件队列
     * <p>接收订单支付成功后的通知消息。</p>
     */
    @Bean
    public Queue orderPaidQueue(@Value("${youxuan.mq.queue.order.paid}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * 订单发货事件队列
     * <p>接收订单发货后的通知消息。</p>
     */
    @Bean
    public Queue orderShippedQueue(@Value("${youxuan.mq.queue.order.shipped}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * 订单取消事件队列
     * <p>接收订单取消后的通知消息。</p>
     */
    @Bean
    public Queue orderCanceledQueue(@Value("${youxuan.mq.queue.order.canceled}") String queueName) {
        return new Queue(queueName, true);
    }

    // ==================== 支付事件队列 ====================

    /**
     * 支付创建事件队列
     * <p>接收支付单创建后的通知消息。</p>
     */
    @Bean
    public Queue paymentCreatedQueue(@Value("${youxuan.mq.queue.payment.created}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * 支付成功事件队列
     * <p>接收支付成功后的通知消息。</p>
     */
    @Bean
    public Queue paymentSuccessQueue(@Value("${youxuan.mq.queue.payment.success}") String queueName) {
        return new Queue(queueName, true);
    }

    /**
     * 支付失败事件队列
     * <p>接收支付失败后的通知消息。</p>
     */
    @Bean
    public Queue paymentFailedQueue(@Value("${youxuan.mq.queue.payment.failed}") String queueName) {
        return new Queue(queueName, true);
    }

    // ==================== 订单事件绑定 ====================

    /**
     * 订单创建队列绑定到订单交换机，路由键 order.created
     */
    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with("order.created");
    }

    /**
     * 订单支付队列绑定到订单交换机，路由键 order.paid
     */
    @Bean
    public Binding orderPaidBinding(Queue orderPaidQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderPaidQueue).to(orderExchange).with("order.paid");
    }

    /**
     * 订单发货队列绑定到订单交换机，路由键 order.shipped
     */
    @Bean
    public Binding orderShippedBinding(Queue orderShippedQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderShippedQueue).to(orderExchange).with("order.shipped");
    }

    /**
     * 订单取消队列绑定到订单交换机，路由键 order.canceled
     */
    @Bean
    public Binding orderCanceledBinding(Queue orderCanceledQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderCanceledQueue).to(orderExchange).with("order.canceled");
    }

    // ==================== 支付事件绑定 ====================

    /**
     * 支付创建队列绑定到支付交换机，路由键 payment.created
     */
    @Bean
    public Binding paymentCreatedBinding(Queue paymentCreatedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentCreatedQueue).to(paymentExchange).with("payment.created");
    }

    /**
     * 支付成功队列绑定到支付交换机，路由键 payment.success
     */
    @Bean
    public Binding paymentSuccessBinding(Queue paymentSuccessQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentSuccessQueue).to(paymentExchange).with("payment.success");
    }

    /**
     * 支付失败队列绑定到支付交换机，路由键 payment.failed
     */
    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentFailedQueue).to(paymentExchange).with("payment.failed");
    }
}
