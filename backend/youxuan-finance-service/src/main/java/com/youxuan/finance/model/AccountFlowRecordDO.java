package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户流水记录数据对象（AccountFlowRecordDO）。
 * <p>
 * 对应 account_flow_records 表，记录商户账户的资金变动明细，
 * 包括入账、出账、冻结、解冻等操作，用于对账和审计。
 * </p>
 */
public class AccountFlowRecordDO {

    /** 主键 ID */
    private Long id;

    /** 流水编号 */
    private String flowNo;

    /** 商户 ID */
    private Long merchantId;

    /** 业务类型（如订单收款、退款、提现、结算等） */
    private String bizType;

    /** 业务单号 */
    private String bizNo;

    /** 资金方向：IN-收入, OUT-支出, FREEZE-冻结, UNFREEZE-解冻 */
    private String direction;

    /** 变动金额 */
    private BigDecimal amount;

    /** 变动前余额 */
    private BigDecimal balanceBefore;

    /** 变动后余额 */
    private BigDecimal balanceAfter;

    /** 变动后冻结金额 */
    private BigDecimal frozenAfter;

    /** 状态 */
    private String status;

    /** 发生时间 */
    private LocalDateTime occurredAt;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlowNo() { return flowNo; }
    public void setFlowNo(String flowNo) { this.flowNo = flowNo; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }

    public String getBizNo() { return bizNo; }
    public void setBizNo(String bizNo) { this.bizNo = bizNo; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public BigDecimal getFrozenAfter() { return frozenAfter; }
    public void setFrozenAfter(BigDecimal frozenAfter) { this.frozenAfter = frozenAfter; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
