package com.youxuan.order.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 平台介入售后请求体
 * <p>
 * 平台客服对商家和用户无法达成一致的售后单进行仲裁时传入的参数。
 * 平台做出最终决定后，售后单按平台意见流转。
 * </p>
 */
public class PlatformInterveneRequest {

    /** 售后单ID */
    @NotNull(message = "售后单ID不能为空")
    private Long afterSaleId;

    /** 平台决定：APPROVE-同意退款，REJECT-拒绝退款 */
    @NotBlank(message = "平台决定不能为空")
    private String decision;

    /** 最终退款金额（仅 APPROVE 时有效） */
    private BigDecimal amount;

    /** 平台处理意见 */
    private String reason;

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
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
