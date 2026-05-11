package com.youxuan.order.model;

import java.time.LocalDateTime;

/**
 * 购物车数据对象
 * <p>
 * 对应数据库 carts 表，记录用户加入购物车的商品 SKU 及数量信息。
 * 一个用户在每个店铺下可以添加多个 SKU，同一 SKU 在购物车中只有一条记录（通过 user_id + sku_id 唯一约束保证）。
 * </p>
 */
public class CartDO {

    /** 购物车记录ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 店铺ID */
    private Long storeId;

    /** 商品ID */
    private Long productId;

    /** SKU ID */
    private Long skuId;

    /** 购买数量 */
    private Integer quantity;

    /** 是否选中：1-选中，0-未选中 */
    private Integer checked;

    /** 状态：ENABLED-启用，DISABLED-禁用 */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getChecked() {
        return checked;
    }

    public void setChecked(Integer checked) {
        this.checked = checked;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
