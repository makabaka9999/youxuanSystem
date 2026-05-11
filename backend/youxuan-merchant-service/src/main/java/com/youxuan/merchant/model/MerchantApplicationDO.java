package com.youxuan.merchant.model;

import java.time.LocalDateTime;

/**
 * 商家入驻申请数据对象，对应 merchant_applications 表。
 */
public class MerchantApplicationDO {

    /** 入驻申请 ID */
    private Long id;

    /** 商家编号（审核通过后生成） */
    private String merchantNo;

    /** 申请用户 ID（即店主用户） */
    private Long ownerUserId;

    /** 公司名称 */
    private String companyName;

    /** 统一社会信用代码 */
    private String licenseNo;

    /** 联系人姓名 */
    private String contactName;

    /** 联系手机号 */
    private String contactMobile;

    /** 审核状态：PENDING / APPROVED / REJECTED */
    private String auditStatus;

    /** 记录状态：ENABLED / DISABLED */
    private String status;

    /** 驳回原因 */
    private String rejectReason;

    /** 审核通过时间 */
    private LocalDateTime approvedAt;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMerchantNo() { return merchantNo; }
    public void setMerchantNo(String merchantNo) { this.merchantNo = merchantNo; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactMobile() { return contactMobile; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
