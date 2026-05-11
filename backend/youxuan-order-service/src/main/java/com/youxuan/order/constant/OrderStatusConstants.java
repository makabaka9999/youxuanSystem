package com.youxuan.order.constant;

/**
 * 订单业务状态常量定义
 * <p>
 * 统一管理订单、支付、售后等核心业务的状态枚举值，所有代码中引用状态时均使用此类常量，
 * 避免硬编码字符串带来的拼写错误风险。常量的值对应数据库 status 字段中存储的字符串。
 * </p>
 */
public final class OrderStatusConstants {

    // ========== 订单主状态（orders.order_status） ==========

    /** 已创建（待支付） */
    public static final String ORDER_CREATED = "CREATED";
    /** 已支付 */
    public static final String ORDER_PAID = "PAID";
    /** 已发货 */
    public static final String ORDER_SHIPPED = "SHIPPED";
    /** 已完成 */
    public static final String ORDER_COMPLETED = "COMPLETED";
    /** 已取消 */
    public static final String ORDER_CANCELED = "CANCELED";
    /** 退款中 */
    public static final String ORDER_REFUNDING = "REFUNDING";
    /** 已关闭 */
    public static final String ORDER_CLOSED = "CLOSED";

    // ========== 支付状态（orders.pay_status） ==========

    /** 未支付 */
    public static final String PAY_UNPAID = "UNPAID";
    /** 已支付 */
    public static final String PAY_PAID = "PAID";
    /** 已退款 */
    public static final String PAY_REFUNDED = "REFUNDED";
    /** 部分退款 */
    public static final String PAY_PART_REFUNDED = "PART_REFUNDED";

    // ========== 支付单状态（payment_orders.pay_status） ==========

    /** 初始化 */
    public static final String PAYMENT_INIT = "INIT";
    /** 支付中 */
    public static final String PAYMENT_PAYING = "PAYING";
    /** 支付成功 */
    public static final String PAYMENT_SUCCESS = "SUCCESS";
    /** 支付失败 */
    public static final String PAYMENT_FAILED = "FAILED";
    /** 已关闭 */
    public static final String PAYMENT_CLOSED = "CLOSED";

    // ========== 售后状态（after_sales.status） ==========

    /** 申请中 */
    public static final String AFTER_SALE_APPLYING = "APPLYING";
    /** 商家已同意 */
    public static final String AFTER_SALE_MERCHANT_APPROVED = "MERCHANT_APPROVED";
    /** 商家已拒绝 */
    public static final String AFTER_SALE_MERCHANT_REJECTED = "MERCHANT_REJECTED";
    /** 用户已退货 */
    public static final String AFTER_SALE_USER_RETURNED = "USER_RETURNED";
    /** 平台介入中 */
    public static final String AFTER_SALE_PLATFORM_INTERVENING = "PLATFORM_INTERVENING";
    /** 已关闭 */
    public static final String AFTER_SALE_CLOSED = "CLOSED";
    /** 已完成 */
    public static final String AFTER_SALE_COMPLETED = "COMPLETED";

    // ========== 售后类型（after_sales.type） ==========

    /** 仅退款 */
    public static final String AFTER_SALE_REFUND_ONLY = "REFUND_ONLY";
    /** 退货退款 */
    public static final String AFTER_SALE_RETURN_REFUND = "RETURN_REFUND";

    // ========== 订单明细退款状态（order_items.refund_status） ==========

    /** 未退款 */
    public static final String REFUND_NONE = "NONE";
    /** 退款中 */
    public static final String REFUND_REFUNDING = "REFUNDING";
    /** 已退款 */
    public static final String REFUND_REFUNDED = "REFUNDED";
    /** 部分退款 */
    public static final String REFUND_PART_REFUNDED = "PART_REFUNDED";

    // ========== 操作人类型 ==========

    /** 用户操作 */
    public static final String OPERATOR_USER = "USER";
    /** 商家操作 */
    public static final String OPERATOR_MERCHANT = "MERCHANT";
    /** 平台操作 */
    public static final String OPERATOR_PLATFORM = "PLATFORM";
    /** 系统操作 */
    public static final String OPERATOR_SYSTEM = "SYSTEM";

    // ========== 通用状态 ==========

    /** 启用 */
    public static final String ENABLED = "ENABLED";
    /** 禁用 */
    public static final String DISABLED = "DISABLED";

    /** 工具类，私有构造 */
    private OrderStatusConstants() {
    }
}
