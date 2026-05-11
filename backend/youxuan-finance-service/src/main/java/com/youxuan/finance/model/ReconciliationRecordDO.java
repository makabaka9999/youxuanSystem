package com.youxuan.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 对账记录数据对象（ReconciliationRecordDO）。
 * <p>
 * 对应 reconciliation_records 表，记录平台与支付渠道（微信、支付宝等）
 * 之间的对账差异明细，用于发现和处理长短款等异常情况。
 * </p>
 */
public class ReconciliationRecordDO {

    /** 主键 ID */
    private Long id;

    /** 对账编号 */
    private String reconcileNo;

    /** 支付渠道（WECHAT_PAY / ALIPAY 等） */
    private String channel;

    /** 对账日期 */
    private LocalDate billDate;

    /** 业务类型 */
    private String bizType;

    /** 平台业务单号 */
    private String bizNo;

    /** 第三方交易号 */
    private String thirdTradeNo;

    /** 平台金额 */
    private BigDecimal platformAmount;

    /** 渠道金额 */
    private BigDecimal channelAmount;

    /** 差异类型（金额不一致、平台有渠道无、渠道有平台无等） */
    private String diffType;

    /** 处理状态（UNHANDLED-未处理, RESOLVED-已解决, IGNORED-已忽略） */
    private String handleStatus;

    /** 处理结果说明 */
    private String handleResult;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReconcileNo() { return reconcileNo; }
    public void setReconcileNo(String reconcileNo) { this.reconcileNo = reconcileNo; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }

    public String getBizNo() { return bizNo; }
    public void setBizNo(String bizNo) { this.bizNo = bizNo; }

    public String getThirdTradeNo() { return thirdTradeNo; }
    public void setThirdTradeNo(String thirdTradeNo) { this.thirdTradeNo = thirdTradeNo; }

    public BigDecimal getPlatformAmount() { return platformAmount; }
    public void setPlatformAmount(BigDecimal platformAmount) { this.platformAmount = platformAmount; }

    public BigDecimal getChannelAmount() { return channelAmount; }
    public void setChannelAmount(BigDecimal channelAmount) { this.channelAmount = channelAmount; }

    public String getDiffType() { return diffType; }
    public void setDiffType(String diffType) { this.diffType = diffType; }

    public String getHandleStatus() { return handleStatus; }
    public void setHandleStatus(String handleStatus) { this.handleStatus = handleStatus; }

    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
