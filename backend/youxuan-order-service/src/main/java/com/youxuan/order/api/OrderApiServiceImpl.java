package com.youxuan.order.api;

import com.youxuan.api.order.OrderApiService;
import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.repository.AfterSaleRepository;
import com.youxuan.order.repository.OrderRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 订单服务 Dubbo RPC 实现。
 */
@DubboService
public class OrderApiServiceImpl implements OrderApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApiServiceImpl.class);

    private final OrderRepository orderRepository;
    private final AfterSaleRepository afterSaleRepository;

    public OrderApiServiceImpl(OrderRepository orderRepository, AfterSaleRepository afterSaleRepository) {
        this.orderRepository = orderRepository;
        this.afterSaleRepository = afterSaleRepository;
    }

    @Override
    public int countPendingShipOrders(Long merchantId) {
        // 待发货 = 已支付但未发货的订单
        return orderRepository.countByMerchantIdAndStatus(merchantId, OrderStatusConstants.ORDER_PAID);
    }

    @Override
    public int countPendingAfterSales(Long merchantId) {
        // 售后待处理 = 状态为 APPLYING（申请中）的售后单
        return afterSaleRepository.countByMerchantIdAndStatus(merchantId, "APPLYING");
    }

    @Override
    public BigDecimal sumOrderAmount(Long merchantId, String startDate, String endDate) {
        return orderRepository.sumAmountByMerchantIdAndDate(merchantId, startDate, endDate);
    }
}
