package com.youxuan.merchant.dto;

/**
 * 商家入驻申请请求 DTO。
 */
public class MerchantApplicationRequest {

    /** 公司名称 */
    private String companyName;

    /** 统一社会信用代码 */
    private String licenseNo;

    /** 联系人姓名 */
    private String contactName;

    /** 联系手机号 */
    private String contactMobile;

    /** 备注 */
    private String remark;

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
