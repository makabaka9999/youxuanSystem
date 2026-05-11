package com.youxuan.admin.model;

import java.time.LocalDateTime;

/**
 * 异常单数据对象（ExceptionOrderDO）。
 * <p>
 * 对应 exception_orders 表，记录平台各业务环节产生的异常情况，
 * 包括订单异常、支付异常、退款异常、商品审核异常等，
 * 支持平台运营人员进行异常处理和追踪。
 * </p>
 */
public class ExceptionOrderDO {

    /** 主键 ID */
    private Long id;

    /** 异常单编号 */
    private String exceptionNo;

    /** 异常类型（如 ORDER_EXCEPTION、PAYMENT_EXCEPTION、REFUND_EXCEPTION、PRODUCT_AUDIT_FAIL 等） */
    private String exceptionType;

    /** 关联业务单号 */
    private String bizNo;

    /** 关联订单 ID */
    private Long orderId;

    /** 关联商户 ID */
    private Long merchantId;

    /** 严重程度（LOW / MEDIUM / HIGH / CRITICAL） */
    private String severity;

    /** 处理状态（PENDING / PROCESSING / RESOLVED / CLOSED） */
    private String status;

    /** 异常原因描述 */
    private String reason;

    /** 处理建议 */
    private String suggestion;

    /** 处理结果说明 */
    private String handleResult;

    /** 处理人用户 ID */
    private Long handledBy;

    /** 处理时间 */
    private LocalDateTime handledAt;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 逻辑删除时间 */
    private LocalDateTime deletedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExceptionNo() { return exceptionNo; }
    public void setExceptionNo(String exceptionNo) { this.exceptionNo = exceptionNo; }

    public String getExceptionType() { return exceptionType; }
    public void setExceptionType(String exceptionType) { this.exceptionType = exceptionType; }

    public String getBizNo() { return bizNo; }
    public void setBizNo(String bizNo) { this.bizNo = bizNo; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }

    public Long getHandledBy() { return handledBy; }
    public void setHandledBy(Long handledBy) { this.handledBy = handledBy; }

    public LocalDateTime getHandledAt() { return handledAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
