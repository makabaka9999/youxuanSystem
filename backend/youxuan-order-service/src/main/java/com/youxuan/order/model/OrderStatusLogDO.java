package com.youxuan.order.model;

import java.time.LocalDateTime;

/**
 * 订单状态变更日志数据对象
 * <p>
 * 对应数据库 order_status_logs 表，记录订单状态的每一次变更轨迹，用于审计和对账。
 * 每次订单状态变更（如创建、支付、发货、完成、取消等）均应插入一条日志。
 * </p>
 */
public class OrderStatusLogDO {

    /** 日志ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 变更前状态 */
    private String fromStatus;

    /** 变更后状态 */
    private String toStatus;

    /** 操作人类型：USER, MERCHANT, PLATFORM, SYSTEM */
    private String operatorType;

    /** 操作人ID */
    private Long operatorId;

    /** 操作原因 */
    private String reason;

    /** 请求ID，用于链路追踪 */
    private String requestId;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
