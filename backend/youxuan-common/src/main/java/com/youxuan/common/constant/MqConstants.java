package com.youxuan.common.constant;

public final class MqConstants {

    public static final String ORDER_EXCHANGE = "youxuan.order.exchange";
    public static final String PAYMENT_EXCHANGE = "youxuan.payment.exchange";
    public static final String FINANCE_EXCHANGE = "youxuan.finance.exchange";

    public static final String ORDER_TIMEOUT_QUEUE = "youxuan.order.timeout.queue";
    public static final String PAYMENT_CALLBACK_QUEUE = "youxuan.payment.callback.queue";
    public static final String STOCK_COMPENSATE_QUEUE = "youxuan.stock.compensate.queue";
    public static final String BILL_GENERATE_QUEUE = "youxuan.bill.generate.queue";

    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";
    public static final String PAYMENT_CALLBACK_ROUTING_KEY = "payment.callback";
    public static final String STOCK_COMPENSATE_ROUTING_KEY = "stock.compensate";
    public static final String BILL_GENERATE_ROUTING_KEY = "bill.generate";

    private MqConstants() {
    }
}
