package com.youxuan.auth.service;

import com.youxuan.auth.config.AuthSecurityProperties;
import com.youxuan.auth.domain.PrincipalTypeEnum;
import com.youxuan.auth.dto.CurrentPrincipalDTO;
import com.youxuan.auth.dto.LoginCommand;
import com.youxuan.auth.dto.LoginResultDTO;
import com.youxuan.auth.model.AuthAccountDO;
import com.youxuan.auth.repository.AuthAccountRepository;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.exception.BizException;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService {

    private static final String ENABLED_STATUS = "ENABLED";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    private final AuthAccountRepository authAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthSecurityProperties authSecurityProperties;

    public AuthApplicationService(AuthAccountRepository authAccountRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtTokenService jwtTokenService,
                                  AuthSecurityProperties authSecurityProperties) {
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authSecurityProperties = authSecurityProperties;
    }

    public LoginResultDTO login(LoginCommand loginCommand) {
        PrincipalTypeEnum principalTypeEnum = PrincipalTypeEnum.valueOf(loginCommand.getPrincipalType().toUpperCase(Locale.ROOT));
        AuthAccountDO authAccountDO = authAccountRepository.findByAccount(principalTypeEnum, loginCommand.getAccount())
                .orElseThrow(() -> new BizException(ErrorCode.AUTH_REQUIRED, "账号或密码错误"));
        if (!ENABLED_STATUS.equals(authAccountDO.getStatus())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "账号已停用");
        }
        if (!passwordEncoder.matches(loginCommand.getPassword(), authAccountDO.getPasswordHash())) {
            throw new BizException(ErrorCode.AUTH_REQUIRED, "账号或密码错误");
        }
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setAccessToken(jwtTokenService.createAccessToken(authAccountDO));
        loginResultDTO.setTokenType(BEARER_TOKEN_TYPE);
        loginResultDTO.setExpiresInSeconds(authSecurityProperties.getAccessTokenTtlMinutes() * 60L);
        loginResultDTO.setCurrentPrincipal(toCurrentPrincipalDTO(authAccountDO));
        return loginResultDTO;
    }

    private CurrentPrincipalDTO toCurrentPrincipalDTO(AuthAccountDO authAccountDO) {
        CurrentPrincipalDTO currentPrincipalDTO = new CurrentPrincipalDTO();
        currentPrincipalDTO.setPrincipalId(authAccountDO.getPrincipalId());
        currentPrincipalDTO.setPrincipalType(authAccountDO.getPrincipalType().name());
        currentPrincipalDTO.setUserId(authAccountDO.getUserId());
        currentPrincipalDTO.setMerchantId(authAccountDO.getMerchantId());
        currentPrincipalDTO.setAccount(authAccountDO.getAccount());
        currentPrincipalDTO.setDisplayName(authAccountDO.getDisplayName());
        currentPrincipalDTO.setRoleCodeSet(authAccountDO.getRoleCodeSet());
        currentPrincipalDTO.setPermissionCodeSet(authAccountDO.getPermissionCodeSet());
        return currentPrincipalDTO;
    }
}
