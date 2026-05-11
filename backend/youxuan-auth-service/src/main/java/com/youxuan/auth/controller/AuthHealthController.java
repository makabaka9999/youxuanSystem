package com.youxuan.auth.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_PREFIX)
public class AuthHealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> health() {
        Map<String, String> resultMap = new LinkedHashMap<>();
        resultMap.put("status", "UP");
        resultMap.put("serviceName", "youxuan-auth-service");
        return ApiResponse.success(resultMap, RequestContext.getRequestId());
    }
}
