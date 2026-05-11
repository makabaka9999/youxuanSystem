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

/**
 * 认证应用服务（Application Service）。
 * <p>
 * 认证模块的核心业务服务，负责编排密码登录流程，
 * 包括账号查找、密码解密与校验、JWT 令牌生成以及登录结果组装。
 * </p>
 */
@Service
public class AuthApplicationService {

    /** 账号启用状态标识 */
    private static final String ENABLED_STATUS = "ENABLED";

    /** Bearer 令牌类型标识 */
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    /** 账号仓储，用于查询账号信息 */
    private final AuthAccountRepository authAccountRepository;

    /** 密码编码器，用于校验密码哈希 */
    private final PasswordEncoder passwordEncoder;

    /** JWT 令牌服务，用于生成访问令牌 */
    private final JwtTokenService jwtTokenService;

    /** 安全配置属性 */
    private final AuthSecurityProperties authSecurityProperties;

    /** RSA 密钥服务，用于解密前端加密的密码 */
    private final RsaKeyService rsaKeyService;

    /**
     * 构造认证应用服务。
     *
     * @param authAccountRepository  账号仓储
     * @param passwordEncoder        密码编码器
     * @param jwtTokenService        JWT 令牌服务
     * @param authSecurityProperties 安全配置属性
     * @param rsaKeyService          RSA 密钥服务
     */
    public AuthApplicationService(AuthAccountRepository authAccountRepository,
                                  PasswordEncoder passwordEncoder,
                                  JwtTokenService jwtTokenService,
                                  AuthSecurityProperties authSecurityProperties,
                                  RsaKeyService rsaKeyService) {
        this.authAccountRepository = authAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authSecurityProperties = authSecurityProperties;
        this.rsaKeyService = rsaKeyService;
    }

    /**
     * 执行密码登录认证。
     * <p>
     * 登录流程：
     * <ol>
     *   <li>根据主体类型和账号查询账号信息；</li>
     *   <li>检查账号状态是否为启用；</li>
     *   <li>使用 RSA 私钥解密前端传入的加密密码；</li>
     *   <li>使用 PasswordEncoder 校验解密后的密码与数据库哈希是否匹配；</li>
     *   <li>生成 JWT 访问令牌并组装登录结果。</li>
     * </ol>
     * </p>
     *
     * @param loginCommand 登录命令，包含账号、密码和主体类型
     * @return 登录结果，包含访问令牌和当前主体信息
     * @throws BizException 当账号不存在、已停用或密码错误时抛出
     */
    public LoginResultDTO login(LoginCommand loginCommand) {
        // 将主体类型字符串转为枚举
        PrincipalTypeEnum principalTypeEnum = PrincipalTypeEnum.valueOf(loginCommand.getPrincipalType().toUpperCase(Locale.ROOT));
        // 1. 查询账号信息
        AuthAccountDO authAccountDO = authAccountRepository.findByAccount(principalTypeEnum, loginCommand.getAccount())
                .orElseThrow(() -> new BizException(ErrorCode.AUTH_REQUIRED, "账号或密码错误"));
        // 2. 检查账号状态
        if (!ENABLED_STATUS.equals(authAccountDO.getStatus())) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "账号已停用");
        }
        // 3. 解密前端 RSA 加密的密码
        String decryptedPassword = rsaKeyService.decrypt(loginCommand.getPassword());
        // 4. 校验密码是否匹配
        if (!passwordEncoder.matches(decryptedPassword, authAccountDO.getPasswordHash())) {
            throw new BizException(ErrorCode.AUTH_REQUIRED, "账号或密码错误");
        }
        // 5. 组装登录结果
        LoginResultDTO loginResultDTO = new LoginResultDTO();
        loginResultDTO.setAccessToken(jwtTokenService.createAccessToken(authAccountDO));
        loginResultDTO.setTokenType(BEARER_TOKEN_TYPE);
        loginResultDTO.setExpiresInSeconds(authSecurityProperties.getAccessTokenTtlMinutes() * 60L);
        loginResultDTO.setCurrentPrincipal(toCurrentPrincipalDTO(authAccountDO));
        return loginResultDTO;
    }

    /**
     * 将账号数据对象转换为当前主体信息 DTO。
     *
     * @param authAccountDO 账号数据对象
     * @return 当前主体信息 DTO
     */
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
