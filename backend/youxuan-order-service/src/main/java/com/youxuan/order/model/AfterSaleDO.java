package com.youxuan.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后单数据对象
 * <p>
 * 对应数据库 after_sales 表，记录用户提交的售后申请信息，包括售后类型、状态、金额及处理意见。
 * 支持两种售后类型：仅退款（REFUND_ONLY）和退货退款（RETURN_REFUND）。
 * 售后单的流转经过：用户申请 -> 商家处理 -> （退货退款还需用户退货） -> 完成。
 * </p>
 */
public class AfterSaleDO {

    /** 售后单ID */
    private Long id;

    /** 售后单号 */
    private String afterSaleNo;

    /** 订单ID */
    private Long orderId;

    /** 订单项ID */
    private Long orderItemId;

    /** 用户ID */
    private Long userId;

    /** 商家ID */
    private Long merchantId;

    /** 售后类型：REFUND_ONLY-仅退款，RETURN_REFUND-退货退款 */
    private String type;

    /** 售后状态：APPLYING, MERCHANT_APPROVED, MERCHANT_REJECTED, USER_RETURNED, PLATFORM_INTERVENING, CLOSED, COMPLETED */
    private String status;

    /** 申请退款金额 */
    private BigDecimal applyAmount;

    /** 同意退款金额 */
    private BigDecimal approvedAmount;

    /** 申请原因 */
    private String reason;

    /** 说明 */
    private String description;

    /** 凭证图片URL列表（JSON数组格式） */
    private String evidenceUrls;

    /** 商家处理意见 */
    private String merchantReason;

    /** 平台处理意见 */
    private String platformReason;

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

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getApprovedAmount() {
        return approvedAmount;
    }

    public void setApprovedAmount(BigDecimal approvedAmount) {
        this.approvedAmount = approvedAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceUrls() {
        return evidenceUrls;
    }

    public void setEvidenceUrls(String evidenceUrls) {
        this.evidenceUrls = evidenceUrls;
    }

    public String getMerchantReason() {
        return merchantReason;
    }

    public void setMerchantReason(String merchantReason) {
        this.merchantReason = merchantReason;
    }

    public String getPlatformReason() {
        return platformReason;
    }

    public void setPlatformReason(String platformReason) {
        this.platformReason = platformReason;
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
