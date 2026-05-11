package com.youxuan.order.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 提交退货物流请求体
 * <p>
 * 用户发起退货退款售后并被商家同意后，填写退货物流信息时传入的参数。
 * </p>
 */
public class ReturnShipmentSubmitRequest {

    /** 售后单ID */
    @NotNull(message = "售后单ID不能为空")
    private Long afterSaleId;

    /** 物流公司 */
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    /** 运单号 */
    @NotBlank(message = "运单号不能为空")
    private String trackingNo;

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
}
