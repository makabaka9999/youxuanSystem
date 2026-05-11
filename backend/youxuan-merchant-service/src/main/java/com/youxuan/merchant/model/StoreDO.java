package com.youxuan.merchant.model;

import java.time.LocalDateTime;

/**
 * 店铺数据对象，对应 stores 表。
 */
public class StoreDO {

    /** 店铺 ID */
    private Long id;

    /** 商家 ID */
    private Long merchantId;

    /** 店铺编号 */
    private String storeNo;

    /** 店铺名称 */
    private String storeName;

    /** Logo 图片 URL */
    private String logoUrl;

    /** 联系手机号 */
    private String contactMobile;

    /** 主营类目 ID */
    private Long categoryId;

    /** 状态：ENABLED / DISABLED */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public String getStoreNo() { return storeNo; }
    public void setStoreNo(String storeNo) { this.storeNo = storeNo; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
