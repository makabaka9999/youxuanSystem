package com.youxuan.auth.controller;

import com.youxuan.auth.dto.LoginCommand;
import com.youxuan.auth.dto.LoginResultDTO;
import com.youxuan.auth.service.AuthApplicationService;
import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/password-login")
    public ApiResponse<LoginResultDTO> passwordLogin(@Valid @RequestBody LoginCommand loginCommand) {
        return ApiResponse.success(authApplicationService.login(loginCommand), RequestContext.getRequestId());
    }
}
