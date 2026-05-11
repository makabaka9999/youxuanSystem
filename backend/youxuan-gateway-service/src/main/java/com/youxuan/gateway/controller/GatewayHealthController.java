package com.youxuan.gateway.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import com.youxuan.gateway.dto.GatewayHealthDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_PREFIX)
public class GatewayHealthController {

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
