package com.youxuan.platform.system.controller;

import com.youxuan.platform.common.api.ApiResponse;
import com.youxuan.platform.common.web.RequestIdHolder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", "youxuan-platform-backend");
        data.put("apiPrefix", "/api/v1");
        return ApiResponse.success(data, RequestIdHolder.get());
    }
}
