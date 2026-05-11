package com.youxuan.merchant.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品数据对象（商家模块视角），对应 products 表。
 * 商家模块直接查询商品数据库以实现商品管理功能。
 */
public class ProductDO {

    /** 商品 ID */
    private Long id;

    /** 商品编号 */
    private String productNo;

    /** 商家 ID */
    private Long merchantId;

    /** 店铺 ID */
    private Long storeId;

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

    /** 审核状态：PENDING / APPROVED / REJECTED */
    private String auditStatus;

    /** 销售状态：ON_SALE / OFF_SALE */
    private String saleStatus;

    /** 驳回原因 */
    private String rejectReason;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductNo() { return productNo; }
    public void setProductNo(String productNo) { this.productNo = productNo; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
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
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getSaleStatus() { return saleStatus; }
    public void setSaleStatus(String saleStatus) { this.saleStatus = saleStatus; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
