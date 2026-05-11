package com.youxuan.merchant.dto;

import java.math.BigDecimal;

/**
 * 商家工作台首页仪表盘数据 DTO。
 */
public class DashboardDTO {

    /** 今日订单数 */
    private Integer todayOrderCount;

    /** 今日销售额 */
    private BigDecimal todayRevenue;

    /** 上月订单数 */
    private Integer lastMonthOrderCount;

    /** 上月销售额 */
    private BigDecimal lastMonthRevenue;

    /** 商品总数 */
    private Integer totalProductCount;

    /** 已上架商品数 */
    private Integer onSaleProductCount;

    /** 待审核商品数 */
    private Integer pendingAuditProductCount;

    public Integer getTodayOrderCount() { return todayOrderCount; }
    public void setTodayOrderCount(Integer todayOrderCount) { this.todayOrderCount = todayOrderCount; }
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
    public Integer getLastMonthOrderCount() { return lastMonthOrderCount; }
    public void setLastMonthOrderCount(Integer lastMonthOrderCount) { this.lastMonthOrderCount = lastMonthOrderCount; }
    public BigDecimal getLastMonthRevenue() { return lastMonthRevenue; }
    public void setLastMonthRevenue(BigDecimal lastMonthRevenue) { this.lastMonthRevenue = lastMonthRevenue; }
    public Integer getTotalProductCount() { return totalProductCount; }
    public void setTotalProductCount(Integer totalProductCount) { this.totalProductCount = totalProductCount; }
    public Integer getOnSaleProductCount() { return onSaleProductCount; }
    public void setOnSaleProductCount(Integer onSaleProductCount) { this.onSaleProductCount = onSaleProductCount; }
    public Integer getPendingAuditProductCount() { return pendingAuditProductCount; }
    public void setPendingAuditProductCount(Integer pendingAuditProductCount) { this.pendingAuditProductCount = pendingAuditProductCount; }
}
