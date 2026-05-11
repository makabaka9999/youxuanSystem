package com.youxuan.merchant.model;

import java.time.LocalDateTime;

/**
 * 商家结算账户数据对象，对应 merchant_settlement_accounts 表。
 */
public class SettlementAccountDO {

    /** 结算账户 ID */
    private Long id;

    /** 商家 ID */
    private Long merchantId;

    /** 账户类型：BANK_CARD / ALIPAY / WECHAT */
    private String accountType;

    /** 开户名称 */
    private String accountName;

    /** 加密的账号 */
    private String accountNoEncrypted;

    /** 脱敏展示的账号（如尾号四位） */
    private String accountNoMasked;

    /** 开户银行名称 */
    private String bankName;

    /** 审核状态：PENDING / APPROVED / REJECTED */
    private String auditStatus;

    /** 记录状态：ENABLED / DISABLED */
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
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountNoEncrypted() { return accountNoEncrypted; }
    public void setAccountNoEncrypted(String accountNoEncrypted) { this.accountNoEncrypted = accountNoEncrypted; }
    public String getAccountNoMasked() { return accountNoMasked; }
    public void setAccountNoMasked(String accountNoMasked) { this.accountNoMasked = accountNoMasked; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAuditStatus() { return auditStatus; }
    public void setAuditStatus(String auditStatus) { this.auditStatus = auditStatus; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
