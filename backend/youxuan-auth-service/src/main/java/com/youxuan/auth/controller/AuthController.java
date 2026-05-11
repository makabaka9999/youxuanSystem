package com.youxuan.auth.controller;

import com.youxuan.auth.dto.LoginCommand;
import com.youxuan.auth.dto.LoginResultDTO;
import com.youxuan.auth.service.AuthApplicationService;
import com.youxuan.auth.service.RsaKeyService;
import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;
    private final RsaKeyService rsaKeyService;

    public AuthController(AuthApplicationService authApplicationService, RsaKeyService rsaKeyService) {
        this.authApplicationService = authApplicationService;
        this.rsaKeyService = rsaKeyService;
    }

    @GetMapping("/public-key")
    public ApiResponse<Map<String, String>> getPublicKey() {
        Map<String, String> data = new HashMap<>();
        data.put("key", rsaKeyService.getPublicKeyBase64());
        data.put("algorithm", "RSA-2048");
        return ApiResponse.success(data, RequestContext.getRequestId());
    }

    @PostMapping("/password-login")
    public ApiResponse<LoginResultDTO> passwordLogin(@Valid @RequestBody LoginCommand loginCommand) {
        return ApiResponse.success(authApplicationService.login(loginCommand), RequestContext.getRequestId());
    }
}
