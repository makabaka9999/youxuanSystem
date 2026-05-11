package com.youxuan.order.dto;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建售后请求体
 * <p>
 * 用户提交售后申请时传入的参数，包括售后类型、关联订单项、退款金额及原因等。
 * </p>
 */
public class AfterSaleCreateRequest {

    /** 订单ID */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /** 订单项ID（为空表示整单售后） */
    private Long orderItemId;

    /** 售后类型：REFUND_ONLY-仅退款，RETURN_REFUND-退货退款 */
    @NotBlank(message = "售后类型不能为空")
    private String type;

    /** 申请退款金额 */
    @NotNull(message = "退款金额不能为空")
    private BigDecimal applyAmount;

    /** 申请原因 */
    @NotBlank(message = "申请原因不能为空")
    private String reason;

    /** 说明 */
    private String description;

    /** 凭证图片URL列表 */
    private List<String> evidenceUrls;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
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

    public List<String> getEvidenceUrls() {
        return evidenceUrls;
    }

    public void setEvidenceUrls(List<String> evidenceUrls) {
        this.evidenceUrls = evidenceUrls;
    }
}
