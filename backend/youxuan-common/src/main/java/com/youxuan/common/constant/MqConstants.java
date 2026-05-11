package com.youxuan.common.constant;

/**
 * RabbitMQ 消息队列常量
 * <p>
 * 统一管理交换机(Exchange)、队列(Queue)和路由键(RoutingKey)的名称，
 * 确保生产者和消费者使用一致的名称进行消息通信。
 * </p>
 */
public final class MqConstants {

    // ==================== 交换机名称 ====================
    /** 订单业务交换机：处理订单超时取消、库存补偿等 */
    public static final String ORDER_EXCHANGE = "youxuan.order.exchange";
    /** 支付业务交换机：处理支付异步回调通知 */
    public static final String PAYMENT_EXCHANGE = "youxuan.payment.exchange";
    /** 资金结算交换机：处理账单生成、对账等 */
    public static final String FINANCE_EXCHANGE = "youxuan.finance.exchange";

    // ==================== 队列名称 ====================
    /** 订单超时未支付取消队列 */
    public static final String ORDER_TIMEOUT_QUEUE = "youxuan.order.timeout.queue";
    /** 支付回调通知队列 */
    public static final String PAYMENT_CALLBACK_QUEUE = "youxuan.payment.callback.queue";
    /** 库存补偿（订单取消/退款后退库存）队列 */
    public static final String STOCK_COMPENSATE_QUEUE = "youxuan.stock.compensate.queue";
    /** 商家结算账单生成队列 */
    public static final String BILL_GENERATE_QUEUE = "youxuan.bill.generate.queue";

    // ==================== 路由键 ====================
    /** 订单超时路由键 */
    public static final String ORDER_TIMEOUT_ROUTING_KEY = "order.timeout";
    /** 支付回调路由键 */
    public static final String PAYMENT_CALLBACK_ROUTING_KEY = "payment.callback";
    /** 库存补偿路由键 */
    public static final String STOCK_COMPENSATE_ROUTING_KEY = "stock.compensate";
    /** 账单生成路由键 */
    public static final String BILL_GENERATE_ROUTING_KEY = "bill.generate";

    /** 工具类，私有构造防止实例化 */
    private MqConstants() {
    }
}
