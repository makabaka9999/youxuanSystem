package com.youxuan.order.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 创建订单的商品项
 * <p>
 * 描述用户下单时选择的某个 SKU 及其购买数量。
 * </p>
 */
public class OrderCreateItem {

    /** SKU ID */
    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    /** 购买数量 */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
