package com.youxuan.gateway.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import com.youxuan.gateway.dto.GatewayHealthDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网关健康检查控制器
 * <p>
 * 提供 API 健康检查接口，用于负载均衡器、容器编排平台或开发人员验证网关是否正常运行。
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX)
public class GatewayHealthController {

    /**
     * 健康检查接口
     * <p>返回网关服务的运行状态、服务名称、API前缀和架构信息。</p>
     *
     * @return 健康状态信息
     */
    @GetMapping("/health")
    public ApiResponse<GatewayHealthDTO> health() {
        GatewayHealthDTO gatewayHealthDTO = new GatewayHealthDTO();
        gatewayHealthDTO.setStatus("UP");
        gatewayHealthDTO.setServiceName("youxuan-gateway-service");
        gatewayHealthDTO.setApiPrefix(ApiConstants.API_PREFIX);
        gatewayHealthDTO.setArchitecture("microservices");
        return ApiResponse.success(gatewayHealthDTO, RequestContext.getRequestId());
    }
}
