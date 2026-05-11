package com.youxuan.order.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商家处理售后请求体
 * <p>
 * 商家对用户的售后申请进行审核时传入的参数，包括审核动作、同意退款金额及原因。
 * </p>
 */
public class MerchantDecisionRequest {

    /** 售后单ID */
    @NotNull(message = "售后单ID不能为空")
    private Long afterSaleId;

    /** 审核动作：APPROVE-同意，REJECT-拒绝 */
    @NotBlank(message = "审核动作不能为空")
    private String action;

    /** 同意退款金额（仅 APPROVE 时有效） */
    private BigDecimal amount;

    /** 审核意见 */
    private String reason;

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
