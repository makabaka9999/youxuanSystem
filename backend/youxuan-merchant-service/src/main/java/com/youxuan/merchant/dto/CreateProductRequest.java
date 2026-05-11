package com.youxuan.merchant.dto;

import java.math.BigDecimal;

/**
 * 商家创建商品请求 DTO。
 */
public class CreateProductRequest {

    /** 类目 ID */
    private Long categoryId;

    /** 商品名称 */
    private String productName;

    /** 主图 URL */
    private String mainImageUrl;

    /** 商品详情 HTML */
    private String detailHtml;

    /** 售价 */
    private BigDecimal price;

    /** 库存总量 */
    private Integer stockTotal;

    /** 备注 */
    private String remark;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public String getDetailHtml() { return detailHtml; }
    public void setDetailHtml(String detailHtml) { this.detailHtml = detailHtml; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
