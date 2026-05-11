package com.youxuan.platform.auth.service;

import com.youxuan.platform.auth.dto.LoginRequest;
import com.youxuan.platform.auth.dto.LoginResponse;
import com.youxuan.platform.common.api.ErrorCode;
import com.youxuan.platform.common.exception.BusinessException;
import com.youxuan.platform.security.config.SecurityProperties;
import com.youxuan.platform.security.domain.AuthAccount;
import com.youxuan.platform.security.domain.AuthPrincipal;
import com.youxuan.platform.security.jwt.JwtTokenProvider;
import com.youxuan.platform.security.repository.AuthAccountRepository;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthAccountRepository authAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;

    public AuthService(AuthAccountRepository authAccountRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       SecurityProperties securityProperties) {
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
    }

    public LoginResponse login(LoginRequest request) {
        String loginType = request.getLoginType() == null ? "USER" : request.getLoginType().toUpperCase(Locale.ROOT);
        AuthAccount account;
        if ("PLATFORM".equals(loginType)) {
            account = authAccountRepository.findPlatformAdminByUsername(request.getAccount())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_REQUIRED, "账号或密码错误"));
        } else {
            account = authAccountRepository.findUserByMobile(request.getAccount())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_REQUIRED, "账号或密码错误"));
        }
        if (!"ENABLED".equals(account.getStatus())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (account.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTH_REQUIRED, "账号或密码错误");
        }
        AuthPrincipal principal = new AuthPrincipal(account);
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtTokenProvider.createAccessToken(principal));
        response.setExpiresInSeconds(securityProperties.getJwt().getAccessTokenTtlMinutes() * 60);
        response.setCurrentUser(toCurrentUser(principal));
        return response;
    }

    public LoginResponse.CurrentUserResponse toCurrentUser(AuthPrincipal principal) {
        LoginResponse.CurrentUserResponse user = new LoginResponse.CurrentUserResponse();
        user.setPrincipalId(principal.getPrincipalId());
        user.setPrincipalType(principal.getPrincipalType().name());
        user.setUserId(principal.getUserId());
        user.setMerchantId(principal.getMerchantId());
        user.setUsername(principal.getUsername());
        user.setDisplayName(principal.getDisplayName());
        user.setRoles(principal.getRoles());
        user.setPermissions(principal.getPermissions());
        return user;
    }
}
