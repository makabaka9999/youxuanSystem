package com.youxuan.auth.service;

import com.youxuan.auth.config.AuthSecurityProperties;
import com.youxuan.auth.model.AuthAccountDO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * JWT 令牌服务。
 * <p>
 * 负责 JWT（JSON Web Token）访问令牌的创建和签名。
 * 使用 HMAC-SHA256 算法对令牌进行签名，令牌中包含主体
 * 标识、主体类型、用户 ID、商户 ID 和角色集合等声明信息。
 * </p>
 */
@Service
public class JwtTokenService {

    /** 安全配置属性，提供签发者和令牌有效期配置 */
    private final AuthSecurityProperties authSecurityProperties;

    /** HMAC 签名密钥 */
    private final Key signingKey;

    /**
     * 构造 JWT 令牌服务。
     *
     * @param authSecurityProperties 安全配置属性
     */
    public JwtTokenService(AuthSecurityProperties authSecurityProperties) {
        this.authSecurityProperties = authSecurityProperties;
        this.signingKey = buildSigningKey(authSecurityProperties.getSecret());
    }

    /**
     * 创建 JWT 访问令牌。
     * <p>
     * 令牌中包含以下自定义声明：principalId、principalType、userId、
     * merchantId 和 roles，以及标准字段 iss、sub、iat 和 exp。
     * 使用 HS256 算法签名。
     * </p>
     *
     * @param authAccountDO 认证账号数据对象
     * @return 签名的 JWT 令牌字符串
     */
    public String createAccessToken(AuthAccountDO authAccountDO) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(authSecurityProperties.getAccessTokenTtlMinutes() * 60L);
        Map<String, Object> claims = new HashMap<>(8);
        claims.put("principalId", authAccountDO.getPrincipalId());
        claims.put("principalType", authAccountDO.getPrincipalType().name());
        claims.put("userId", authAccountDO.getUserId());
        claims.put("merchantId", authAccountDO.getMerchantId());
        claims.put("roles", authAccountDO.getRoleCodeSet());
        if (authAccountDO.getRoleType() != null) {
            claims.put("roleType", authAccountDO.getRoleType());
        }
        return Jwts.builder()
                .setIssuer(authSecurityProperties.getIssuer())
                .setSubject(authAccountDO.getAccount())
                .setClaims(claims)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiredAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 构建 HMAC 签名密钥。
     * <p>
     * 优先将 secret 作为 Base64 字符串解码后生成密钥，
     * 如果解码失败则直接使用 UTF-8 字节数组作为原始密钥。
     * 密钥长度必须至少 32 个字符。
     * </p>
     *
     * @param secret 密钥字符串
     * @return HMAC 签名密钥
     * @throws IllegalStateException 如果密钥长度不足 32 个字符
     */
    private Key buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters");
        }
        try {
            // 优先尝试将 secret 作为 Base64 解码
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (Exception exception) {
            // 如果 secret 不是合法的 Base64，直接使用 UTF-8 字节作为 HMAC 密钥
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
