package com.youxuan.common.config;

import com.youxuan.common.constant.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Queue.class)
public class RabbitMqAutoConfiguration {

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(MqConstants.ORDER_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(MqConstants.PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange financeExchange() {
        return new DirectExchange(MqConstants.FINANCE_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderTimeoutQueue() {
        return new Queue(MqConstants.ORDER_TIMEOUT_QUEUE, true);
    }

    @Bean
    public Queue paymentCallbackQueue() {
        return new Queue(MqConstants.PAYMENT_CALLBACK_QUEUE, true);
    }

    @Bean
    public Queue stockCompensateQueue() {
        return new Queue(MqConstants.STOCK_COMPENSATE_QUEUE, true);
    }

    @Bean
    public Queue billGenerateQueue() {
        return new Queue(MqConstants.BILL_GENERATE_QUEUE, true);
    }

    @Bean
    public Binding orderTimeoutBinding(Queue orderTimeoutQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(orderTimeoutQueue).to(orderExchange).with(MqConstants.ORDER_TIMEOUT_ROUTING_KEY);
    }

    @Bean
    public Binding stockCompensateBinding(Queue stockCompensateQueue, DirectExchange orderExchange) {
        return BindingBuilder.bind(stockCompensateQueue).to(orderExchange).with(MqConstants.STOCK_COMPENSATE_ROUTING_KEY);
    }

    @Bean
    public Binding paymentCallbackBinding(Queue paymentCallbackQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentCallbackQueue).to(paymentExchange).with(MqConstants.PAYMENT_CALLBACK_ROUTING_KEY);
    }

    @Bean
    public Binding billGenerateBinding(Queue billGenerateQueue, DirectExchange financeExchange) {
        return BindingBuilder.bind(billGenerateQueue).to(financeExchange).with(MqConstants.BILL_GENERATE_ROUTING_KEY);
    }
}
