package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 结算单数据对象（SettlementOrderDO）。
 * <p>
 * 对应 settlement_orders 表，记录每期结算单的汇总信息，包含结算周期、
 * 各金额项、审核状态及审核人信息等，是商户提现的前置环节。
 * </p>
 */
public class SettlementOrderDO {

    /** 主键 ID */
    private Long id;

    /** 结算单编号 */
    private String settlementNo;

    /** 商户 ID */
    private Long merchantId;

    /** 结算周期起始日期 */
    private LocalDate startDate;

    /** 结算周期结束日期 */
    private LocalDate endDate;

    /** 订单金额合计 */
    private BigDecimal orderAmount;

    /** 退款金额合计 */
    private BigDecimal refundAmount;

    /** 佣金金额合计 */
    private BigDecimal commissionAmount;

    /** 冻结金额 */
    private BigDecimal frozenAmount;

    /** 应付金额 */
    private BigDecimal payableAmount;

    /** 状态：PENDING_AUDIT-待审核, APPROVED-已通过, REJECTED-已驳回, PAID-已付款 */
    private String status;

    /** 审核人用户 ID */
    private Long auditUserId;

    /** 审核原因/意见 */
    private String auditReason;

    /** 审核通过时间 */
    private LocalDateTime approvedAt;

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

    public String getSettlementNo() { return settlementNo; }
    public void setSettlementNo(String settlementNo) { this.settlementNo = settlementNo; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getFrozenAmount() { return frozenAmount; }
    public void setFrozenAmount(BigDecimal frozenAmount) { this.frozenAmount = frozenAmount; }

    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = payableAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAuditUserId() { return auditUserId; }
    public void setAuditUserId(Long auditUserId) { this.auditUserId = auditUserId; }

    public String getAuditReason() { return auditReason; }
    public void setAuditReason(String auditReason) { this.auditReason = auditReason; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

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
