package com.youxuan.order.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_PREFIX)
public class OrderHealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("status", "UP");
        resultMap.put("serviceName", "youxuan-order-service");
        resultMap.put("responsibilities", Arrays.asList("cart", "order", "orderStateMachine", "shipment", "afterSale"));
        return ApiResponse.success(resultMap, RequestContext.getRequestId());
    }
}
