package com.youxuan.order.dto;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 创建订单请求体
 * <p>
 * 用户提交订单时传入的参数，包含收货地址ID和商品SKU列表。
 * 系统会根据 SKU 所属商家自动拆分订单（一个商家一个订单）。
 * </p>
 */
public class OrderCreateRequest {

    /** 收货地址ID */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /** 商品SKU列表 */
    @NotEmpty(message = "商品列表不能为空")
    @Valid
    private List<OrderCreateItem> items;

    /** 订单备注 */
    private String remark;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public List<OrderCreateItem> getItems() {
        return items;
    }

    public void setItems(List<OrderCreateItem> items) {
        this.items = items;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
