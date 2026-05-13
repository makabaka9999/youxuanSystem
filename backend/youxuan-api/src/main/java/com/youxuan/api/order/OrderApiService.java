package com.youxuan.api.order;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单服务 Dubbo API 接口
 *
 * <p>提供订单查询、售后统计等跨服务调用的能力，供商家、管理等模块通过 Dubbo 远程调用。</p>
 */
public interface OrderApiService {

    /**
     * 获取商家待发货的订单数量。
     *
     * @param merchantId 商家 ID
     * @return 待发货订单数
     */
    int countPendingShipOrders(Long merchantId);

    /**
     * 获取商家售后待处理的数量。
     *
     * @param merchantId 商家 ID
     * @return 售后待处理数
     */
    int countPendingAfterSales(Long merchantId);

    /**
     * 获取商家在指定日期范围内的订单总金额。
     *
     * @param merchantId 商家 ID
     * @param startDate  开始日期，格式 yyyy-MM-dd
     * @param endDate    结束日期，格式 yyyy-MM-dd
     * @return 订单总金额
     */
    BigDecimal sumOrderAmount(Long merchantId, String startDate, String endDate);
}
