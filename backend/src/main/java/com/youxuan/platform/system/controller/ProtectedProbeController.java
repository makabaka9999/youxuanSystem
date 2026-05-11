package com.youxuan.platform.system.controller;

import com.youxuan.platform.common.api.ApiResponse;
import com.youxuan.platform.common.web.RequestIdHolder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/probe")
public class ProtectedProbeController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user:profile:read')")
    public ApiResponse<Map<String, Object>> userProbe() {
        return ApiResponse.success(message("user api connected"), RequestIdHolder.get());
    }

    @GetMapping("/merchant")
    @PreAuthorize("hasAuthority('merchant:dashboard:read')")
    public ApiResponse<Map<String, Object>> merchantProbe() {
        return ApiResponse.success(message("merchant api connected"), RequestIdHolder.get());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin:dashboard:read')")
    public ApiResponse<Map<String, Object>> adminProbe() {
        return ApiResponse.success(message("admin api connected"), RequestIdHolder.get());
    }

    private Map<String, Object> message(String text) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", text);
        return data;
    }
}
