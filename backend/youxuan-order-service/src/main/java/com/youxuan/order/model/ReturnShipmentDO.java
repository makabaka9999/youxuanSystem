package com.youxuan.order.model;

import java.time.LocalDateTime;

/**
 * 退货物流数据对象
 * <p>
 * 对应数据库 return_shipments 表，记录用户在退货退款流程中填写的退货物流信息。
 * 每个售后单只有一条退货物流记录（通过 after_sale_id 唯一约束保证）。
 * </p>
 */
public class ReturnShipmentDO {

    /** 退货物流ID */
    private Long id;

    /** 售后单ID */
    private Long afterSaleId;

    /** 物流公司 */
    private String logisticsCompany;

    /** 运单号 */
    private String trackingNo;

    /** 用户退货时间 */
    private LocalDateTime shippedAt;

    /** 商家收货时间 */
    private LocalDateTime receivedAt;

    /** 状态：SHIPPED-已发货，RECEIVED-已签收 */
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

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Long afterSaleId) {
        this.afterSaleId = afterSaleId;
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

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
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
