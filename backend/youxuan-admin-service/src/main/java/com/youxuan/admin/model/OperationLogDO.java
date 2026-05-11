package com.youxuan.admin.model;

import java.time.LocalDateTime;

/**
 * 操作日志数据对象（OperationLogDO）。
 * <p>
 * 对应 operation_logs 表，记录平台所有操作行为的审计日志，
 * 包括操作人、操作类型、操作对象、请求来源及操作前后的数据快照，
 * 用于安全审计和问题追溯。
 * </p>
 */
public class OperationLogDO {

    /** 主键 ID */
    private Long id;

    /** 请求追踪 ID */
    private String requestId;

    /** 操作人类型（PLATFORM_ADMIN / MERCHANT_STAFF / USER / SYSTEM） */
    private String operatorType;

    /** 操作人用户 ID */
    private Long operatorId;

    /** 操作人显示名称 */
    private String operatorName;

    /** 关联商户 ID */
    private Long merchantId;

    /** 功能模块编码 */
    private String moduleCode;

    /** 操作动作编码 */
    private String actionCode;

    /** 操作目标类型 */
    private String targetType;

    /** 操作目标 ID */
    private String targetId;

    /** 业务类型 */
    private String bizType;

    /** 业务单号 */
    private String bizNo;

    /** 操作前数据快照（JSON 格式） */
    private String beforeSnapshot;

    /** 操作后数据快照（JSON 格式） */
    private String afterSnapshot;

    /** 请求来源 IP */
    private String requestIp;

    /** 用户代理标识 */
    private String userAgent;

    /** 操作结果（SUCCESS / FAILED） */
    private String result;

    /** 失败原因 */
    private String failReason;

    /** 状态 */
    private String status;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getOperatorType() { return operatorType; }
    public void setOperatorType(String operatorType) { this.operatorType = operatorType; }

    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }

    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getModuleCode() { return moduleCode; }
    public void setModuleCode(String moduleCode) { this.moduleCode = moduleCode; }

    public String getActionCode() { return actionCode; }
    public void setActionCode(String actionCode) { this.actionCode = actionCode; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }

    public String getBizNo() { return bizNo; }
    public void setBizNo(String bizNo) { this.bizNo = bizNo; }

    public String getBeforeSnapshot() { return beforeSnapshot; }
    public void setBeforeSnapshot(String beforeSnapshot) { this.beforeSnapshot = beforeSnapshot; }

    public String getAfterSnapshot() { return afterSnapshot; }
    public void setAfterSnapshot(String afterSnapshot) { this.afterSnapshot = afterSnapshot; }

    public String getRequestIp() { return requestIp; }
    public void setRequestIp(String requestIp) { this.requestIp = requestIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
