package com.youxuan.order.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建支付请求体
 * <p>
 * 用户发起支付时传入的参数，指定要支付的订单和支付渠道。
 * P0 阶段仅做支付单创建，不对接真实支付渠道。
 * </p>
 */
public class CreatePaymentRequest {

    /** 订单号 */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /** 支付渠道 */
    @NotBlank(message = "支付渠道不能为空")
    private String channel;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
