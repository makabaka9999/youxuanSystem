package com.youxuan.common.config;

import com.youxuan.common.constant.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 消息队列自动配置类
 * <p>
 * 当 classpath 下存在 {@link Queue} 类（即引入了 spring-rabbit 依赖）时自动生效。
 * 统一声明三大交换机（订单、支付、资金结算）及其绑定的队列和路由规则。
 * </p>
 */
@Configuration
@ConditionalOnClass(Queue.class)
public class RabbitMqAutoConfiguration {

    // ==================== 交换机定义 ====================

    /**
     * 订单业务交换机
     * <p>处理订单超时取消、库存补偿等消息。</p>
     *
     * @return 持久化的 DirectExchange
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(MqConstants.ORDER_EXCHANGE, true, false);
    }

    /**
     * 支付业务交换机
     * <p>处理支付回调通知消息。</p>
     *
     * @return 持久化的 DirectExchange
     */
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(MqConstants.PAYMENT_EXCHANGE, true, false);
    }

    /**
     * 资金结算交换机
     * <p>处理账单生成等消息。</p>
     *
     * @return 持久化的 DirectExchange
     */
    @Bean
    public DirectExchange financeExchange() {
        return new DirectExchange(MqConstants.FINANCE_EXCHANGE, true, false);
    }

    // ==================== 队列定义 ====================

    /**
     * 订单超时队列
     * <p>存储订单超时未支付的消息，消费者从中取出后执行超时取消逻辑。</p>
     *
     * @return 持久化队列
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return new Queue(MqConstants.ORDER_TIMEOUT_QUEUE, true);
    }

    /**
     * 支付回调队列
     * <p>存储第三方支付平台的异步回调通知，消费者从中取出后更新订单支付状态。</p>
     *
     * @return 持久化队列
     */
    @Bean
    public Queue paymentCallbackQueue() {
        return new Queue(MqConstants.PAYMENT_CALLBACK_QUEUE, true);
    }

    /**
     * 库存补偿队列
     * <p>订单超时取消或退款成功后，异步归还库存。</p>
     *
     * @return 持久化队列
     */
    @Bean
    public Queue stockCompensateQueue() {
        return new Queue(MqConstants.STOCK_COMPENSATE_QUEUE, true);
    }

    /**
     * 账单生成队列
     * <p>异步触发生成商家结算账单。</p>
     *
     * @return 持久化队列
     */
    @Bean
    public Queue billGenerateQueue() {
        return new Queue(MqConstants.BILL_GENERATE_QUEUE, true);
    }

    // ==================== 绑定关系 ====================

    /**
     * 订单超时队列绑定到订单交换机
     *
     * @param orderTimeoutQueue 订单超时队列
     * @param orderExchange     订单交换机
     * @return 绑定关系
     */
    @Bean
    public Binding orderTimeoutBinding(Queue orderTimeoutQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderTimeoutQueue).to(orderExchange).with(MqConstants.ORDER_TIMEOUT_ROUTING_KEY);
    }

    /**
     * 库存补偿队列绑定到订单交换机
     *
     * @param stockCompensateQueue 库存补偿队列
     * @param orderExchange        订单交换机
     * @return 绑定关系
     */
    @Bean
    public Binding stockCompensateBinding(Queue stockCompensateQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(stockCompensateQueue).to(orderExchange).with(MqConstants.STOCK_COMPENSATE_ROUTING_KEY);
    }

    /**
     * 支付回调队列绑定到支付交换机
     *
     * @param paymentCallbackQueue 支付回调队列
     * @param paymentExchange      支付交换机
     * @return 绑定关系
     */
    @Bean
    public Binding paymentCallbackBinding(Queue paymentCallbackQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentCallbackQueue).to(paymentExchange).with(MqConstants.PAYMENT_CALLBACK_ROUTING_KEY);
    }

    /**
     * 账单生成队列绑定到资金结算交换机
     *
     * @param billGenerateQueue 账单生成队列
     * @param financeExchange   资金结算交换机
     * @return 绑定关系
     */
    @Bean
    public Binding billGenerateBinding(Queue billGenerateQueue, DirectExchange financeExchange) {
        return BindingBuilder.bind(billGenerateQueue).to(financeExchange).with(MqConstants.BILL_GENERATE_ROUTING_KEY);
    }
}
