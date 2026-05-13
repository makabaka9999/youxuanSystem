package com.youxuan.auth.controller;

import com.youxuan.auth.dto.LoginCommand;
import com.youxuan.auth.dto.LoginResultDTO;
import com.youxuan.auth.dto.RegisterCommand;
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

/**
 * 认证控制器。
 * <p>
 * 提供登录认证相关的 RESTful API 接口，包括获取 RSA 公钥和密码登录等功能。
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/auth")
public class AuthController {

    /** 认证应用服务，处理登录等核心业务逻辑 */
    private final AuthApplicationService authApplicationService;

    /** RSA 密钥服务，用于密码加解密操作 */
    private final RsaKeyService rsaKeyService;

    /**
     * 构造认证控制器。
     *
     * @param authApplicationService 认证应用服务
     * @param rsaKeyService          RSA 密钥服务
     */
    public AuthController(AuthApplicationService authApplicationService, RsaKeyService rsaKeyService) {
        this.authApplicationService = authApplicationService;
        this.rsaKeyService = rsaKeyService;
    }

    /**
     * 获取 RSA 公钥。
     * <p>
     * 前端在登录前调用此接口获取公钥，用于加密登录密码。
     * </p>
     *
     * @return 包含 Base64 编码公钥和算法名称的响应结果
     */
    @GetMapping("/public-key")
    public ApiResponse<Map<String, String>> getPublicKey() {
        Map<String, String> data = new HashMap<>();
        data.put("key", rsaKeyService.getPublicKeyBase64());
        data.put("algorithm", "RSA-2048");
        return ApiResponse.success(data, RequestContext.getRequestId());
    }

    /**
     * 密码登录接口。
     * <p>
     * 接收经过 RSA 加密的登录凭证进行密码登录认证，
     * 认证成功返回 JWT 访问令牌和当前主体信息。
     * </p>
     *
     * @param loginCommand 登录命令，包含账号、密码和主体类型
     * @return 包含访问令牌和当前主体信息的登录结果
     */
    @PostMapping("/password-login")
    public ApiResponse<LoginResultDTO> passwordLogin(@Valid @RequestBody LoginCommand loginCommand) {
        return ApiResponse.success(authApplicationService.login(loginCommand), RequestContext.getRequestId());
    }

    /**
     * 用户注册接口。
     * <p>
     * 使用手机号注册新用户，密码经过 RSA 加密传输，
     * 注册成功自动分配 USER 角色，不返回令牌（需登录）。
     * </p>
     *
     * @param registerCommand 注册信息，包含手机号、密码和昵称
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterCommand registerCommand) {
        authApplicationService.register(registerCommand);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
