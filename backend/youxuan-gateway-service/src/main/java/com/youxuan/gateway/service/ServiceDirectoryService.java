package com.youxuan.gateway.service;

import com.youxuan.gateway.dto.ServiceDirectoryDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ServiceDirectoryService {

    public List<ServiceDirectoryDTO> listServices() {
        return Arrays.asList(
                new ServiceDirectoryDTO("auth", "认证权限服务", "/api/v1/auth", "登录、JWT、RBAC、账号权限"),
                new ServiceDirectoryDTO("product", "商品服务", "/api/v1/products", "类目、商品、SKU、库存快照"),
                new ServiceDirectoryDTO("order", "交易订单服务", "/api/v1/orders", "购物车、下单、订单状态机、履约"),
                new ServiceDirectoryDTO("merchant", "商家服务", "/api/v1/merchant", "入驻、店铺、员工、商家侧操作"),
                new ServiceDirectoryDTO("finance", "资金结算服务", "/api/v1/finance", "支付、退款、账单、结算、提现、对账"),
                new ServiceDirectoryDTO("admin", "平台后台服务", "/api/v1/admin", "审核、异常池、平台介入、审计查询")
        );
    }
}
