package com.youxuan.order.model;

import java.time.LocalDateTime;

/**
 * 订单发货物流数据对象
 * <p>
 * 对应数据库 order_shipments 表，记录商家对订单的发货物流信息。
 * 一个订单只有一条发货记录（通过 order_id 唯一约束保证）。
 * </p>
 */
public class OrderShipmentDO {

    /** 发货物流ID */
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 商家ID */
    private Long merchantId;

    /** 物流公司 */
    private String logisticsCompany;

    /** 运单号 */
    private String trackingNo;

    /** 发货操作人ID */
    private Long shippedBy;

    /** 发货时间 */
    private LocalDateTime shippedAt;

    /** 状态：SHIPPED-已发货，SIGNED-已签收 */
    private String status;

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

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public Long getShippedBy() {
        return shippedBy;
    }

    public void setShippedBy(Long shippedBy) {
        this.shippedBy = shippedBy;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
