package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提现单数据对象（WithdrawOrderDO）。
 * <p>
 * 对应 withdraw_orders 表，记录商户发起提现的申请、审核、
 * 支付处理全流程信息，包含幂等键用于保证提现操作幂等性。
 * </p>
 */
public class WithdrawOrderDO {

    /** 主键 ID */
    private Long id;

    /** 提现单编号 */
    private String withdrawNo;

    /** 商户 ID */
    private Long merchantId;

    /** 收款账户 ID */
    private Long accountId;

    /** 关联结算单 ID */
    private Long settlementId;

    /** 提现金额 */
    private BigDecimal amount;

    /** 状态：PENDING_AUDIT-待审核, APPROVED-已通过, REJECTED-已驳回, PAYING-付款中, SUCCESS-成功, FAILED-失败 */
    private String status;

    /** 审核人用户 ID */
    private Long auditUserId;

    /** 审核原因/意见 */
    private String auditReason;

    /** 支付失败原因 */
    private String failReason;

    /** 付款完成时间 */
    private LocalDateTime paidAt;

    /** 幂等键，保证提现操作幂等性 */
    private String idempotentKey;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 逻辑删除时间 */
    private LocalDateTime deletedAt;

    /** 乐观锁版本号 */
    private Integer version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWithdrawNo() { return withdrawNo; }
    public void setWithdrawNo(String withdrawNo) { this.withdrawNo = withdrawNo; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAuditUserId() { return auditUserId; }
    public void setAuditUserId(Long auditUserId) { this.auditUserId = auditUserId; }

    public String getAuditReason() { return auditReason; }
    public void setAuditReason(String auditReason) { this.auditReason = auditReason; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getIdempotentKey() { return idempotentKey; }
    public void setIdempotentKey(String idempotentKey) { this.idempotentKey = idempotentKey; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
}
