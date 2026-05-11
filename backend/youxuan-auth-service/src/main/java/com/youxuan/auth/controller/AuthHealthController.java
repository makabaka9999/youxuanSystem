package com.youxuan.auth.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证服务健康检查控制器。
 * <p>
 * 提供服务健康检查端点，用于网关和服务注册中心监控认证服务的运行状态。
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX)
public class AuthHealthController {

    /**
     * 健康检查接口。
     * <p>
     * 返回当前服务的运行状态和服务名称，供网关和服务治理组件进行健康探测。
     * </p>
     *
     * @return 包含服务状态和服务名称的健康检查结果
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> resultMap = new LinkedHashMap<>();
        resultMap.put("status", "UP");
        resultMap.put("serviceName", "youxuan-auth-service");
        return ApiResponse.success(resultMap, RequestContext.getRequestId());
    }
}
