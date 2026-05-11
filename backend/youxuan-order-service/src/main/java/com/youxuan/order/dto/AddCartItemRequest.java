package com.youxuan.order.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 添加购物车项请求体
 * <p>
 * 用户向购物车中添加商品 SKU 时传入的参数。
 * 若购物车中已有同一 SKU，则累加数量。
 * </p>
 */
public class AddCartItemRequest {

    /** SKU ID，不可为空 */
    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    /** 购买数量，最小为1 */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量不能小于1")
    private Integer quantity;

    /** 店铺ID */
    @NotNull(message = "店铺ID不能为空")
    private Long storeId;

    /** 商品ID */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

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

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
