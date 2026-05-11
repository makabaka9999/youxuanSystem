package com.youxuan.merchant.dto;

/**
 * 店铺信息更新请求 DTO。
 */
public class StoreUpdateRequest {

    /** 店铺名称 */
    private String storeName;

    /** Logo 图片 URL */
    private String logoUrl;

    /** 联系手机号 */
    private String contactMobile;

    /** 主营类目 ID */
    private Long categoryId;

    /** 备注 */
    private String remark;

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
