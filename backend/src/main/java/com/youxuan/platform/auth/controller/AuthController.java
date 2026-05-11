package com.youxuan.platform.auth.controller;

import com.youxuan.platform.auth.dto.LoginRequest;
import com.youxuan.platform.auth.dto.LoginResponse;
import com.youxuan.platform.auth.service.AuthService;
import com.youxuan.platform.common.api.ApiResponse;
import com.youxuan.platform.common.web.RequestIdHolder;
import com.youxuan.platform.security.domain.AuthPrincipal;
import javax.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/password-login")
    public ApiResponse<LoginResponse> passwordLogin(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request), RequestIdHolder.get());
    }

    @PostMapping("/mobile-login")
    public ApiResponse<LoginResponse> mobileLogin(@Valid @RequestBody LoginRequest request) {
        request.setLoginType("USER");
        return ApiResponse.success(authService.login(request), RequestIdHolder.get());
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse.CurrentUserResponse> me(@AuthenticationPrincipal AuthPrincipal principal) {
        return ApiResponse.success(authService.toCurrentUser(principal), RequestIdHolder.get());
    }
}
