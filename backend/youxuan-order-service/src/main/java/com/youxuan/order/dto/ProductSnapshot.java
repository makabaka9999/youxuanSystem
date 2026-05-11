package com.youxuan.order.dto;

import java.math.BigDecimal;

/**
 * 商品快照
 * <p>
 * 下单时从商品 SKU 中提取的商品信息快照，序列化为 JSON 后存储在 order_items 表的 product_snapshot 字段中。
 * 后续退款、售后等流程以该快照为准，不受商家后续修改商品信息的影响。
 * </p>
 */
public class ProductSnapshot {

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 主图URL */
    private String mainImageUrl;

    /** SKU ID */
    private Long skuId;

    /** SKU名称 */
    private String skuName;

    /** 成交单价 */
    private BigDecimal salePrice;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
}
