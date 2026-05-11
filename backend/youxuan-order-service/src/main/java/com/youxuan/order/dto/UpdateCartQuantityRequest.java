package com.youxuan.order.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新购物车数量请求体
 * <p>
 * 用户修改购物车中某个商品 SKU 的购买数量时传入的参数。
 * </p>
 */
public class UpdateCartQuantityRequest {

    /** 新的购买数量，最小为1 */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
