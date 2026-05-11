package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商户账单数据对象（MerchantBillDO）。
 * <p>
 * 对应 merchant_bills 表，记录每个商户每日的订单金额、运费、退款、佣金、
 * 调整金额及最终应付金额等财务汇总数据，是商户结算的基础数据源。
 * </p>
 */
public class MerchantBillDO {

    /** 主键 ID */
    private Long id;

    /** 商户 ID */
    private Long merchantId;

    /** 账单日期 */
    private LocalDate billDate;

    /** 订单金额合计 */
    private BigDecimal orderAmount;

    /** 运费金额合计 */
    private BigDecimal freightAmount;

    /** 退款金额合计 */
    private BigDecimal refundAmount;

    /** 佣金金额合计 */
    private BigDecimal commissionAmount;

    /** 调整金额（平台手动调整，可正可负） */
    private BigDecimal adjustmentAmount;

    /** 应付金额（计算后最终应付） */
    private BigDecimal payableAmount;

    /** 账单状态：GENERATED-已生成, CONFIRMED-已确认, SETTLED-已结算 */
    private String status;

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

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getAdjustmentAmount() { return adjustmentAmount; }
    public void setAdjustmentAmount(BigDecimal adjustmentAmount) { this.adjustmentAmount = adjustmentAmount; }

    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = payableAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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
