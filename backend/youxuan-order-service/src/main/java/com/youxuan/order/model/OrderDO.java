package com.youxuan.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表数据对象
 * <p>
 * 对应数据库 orders 表，记录订单的核心信息，包括订单状态、支付状态、金额、收货信息快照等。
 * 订单金额计算关系：totalAmount + freightAmount - discountAmount = payableAmount。
 * 每个订单归属于一个商家（merchantId）和一个店铺（storeId）。
 * </p>
 */
public class OrderDO {

    /** 订单ID */
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 商家ID */
    private Long merchantId;

    /** 店铺ID */
    private Long storeId;

    /** 订单状态：CREATED, PAID, SHIPPED, COMPLETED, CANCELED, REFUNDING, CLOSED */
    private String orderStatus;

    /** 支付状态：UNPAID, PAID, REFUNDED, PART_REFUNDED */
    private String payStatus;

    /** 商品总额 */
    private BigDecimal totalAmount;

    /** 运费 */
    private BigDecimal freightAmount;

    /** 优惠金额（P0默认0） */
    private BigDecimal discountAmount;

    /** 应付金额 */
    private BigDecimal payableAmount;

    /** 实付金额 */
    private BigDecimal paidAmount;

    /** 收货信息快照（JSON格式，含收件人、手机号、地址等） */
    private String receiverSnapshot;

    /** 支付时间 */
    private LocalDateTime paidAt;

    /** 发货时间 */
    private LocalDateTime shippedAt;

    /** 完成时间 */
    private LocalDateTime completedAt;

    /** 取消时间 */
    private LocalDateTime canceledAt;

    /** 取消原因 */
    private String cancelReason;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFreightAmount() {
        return freightAmount;
    }

    public void setFreightAmount(BigDecimal freightAmount) {
        this.freightAmount = freightAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getReceiverSnapshot() {
        return receiverSnapshot;
    }

    public void setReceiverSnapshot(String receiverSnapshot) {
        this.receiverSnapshot = receiverSnapshot;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
