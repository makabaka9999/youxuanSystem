package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 结算单明细数据对象（SettlementItemDO）。
 * <p>
 * 对应 settlement_order_items 表，记录结算单中包含的每一笔订单明细，
 * 用于追踪结算单的构成明细，支持商家核对应结算款项。
 * </p>
 */
public class SettlementItemDO {

    /** 主键 ID */
    private Long id;

    /** 结算单 ID */
    private Long settlementId;

    /** 商户 ID */
    private Long merchantId;

    /** 订单 ID */
    private Long orderId;

    /** 订单项 ID */
    private Long orderItemId;

    /** 关联账单 ID */
    private Long billId;

    /** 应付金额 */
    private BigDecimal payableAmount;

    /** 佣金金额 */
    private BigDecimal commissionAmount;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }

    public BigDecimal getPayableAmount() { return payableAmount; }
    public void setPayableAmount(BigDecimal payableAmount) { this.payableAmount = payableAmount; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
