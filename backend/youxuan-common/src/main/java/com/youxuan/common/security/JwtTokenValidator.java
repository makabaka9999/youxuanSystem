package com.youxuan.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * JWT 令牌验证器，从 accessToken 中解析 JwtClaims。
 * 签名算法与 auth-service 的 JwtTokenService 保持一致（HS256）。
 */
public class JwtTokenValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenValidator.class);

    private final Key signingKey;

    public JwtTokenValidator(String secret) {
        this.signingKey = buildSigningKey(secret);
    }

    /**
     * 验证 accessToken 并返回解析后的认证信息。
     *
     * @param accessToken Bearer token 字符串（不含 "Bearer " 前缀）
     * @return 解析成功返回 JwtClaims，失败返回 null
     */
    public JwtClaims validate(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            return null;
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();

            Long principalId = claims.get("principalId", Long.class);
            String principalType = claims.get("principalType", String.class);
            Long userId = claims.get("userId", Long.class);
            Long merchantId = claims.get("merchantId", Long.class);
            String displayName = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> roleList = claims.get("roles", List.class);
            Set<String> roles = roleList != null ? new HashSet<>(roleList) : Collections.emptySet();

            String roleType = claims.get("roleType", String.class);

            return new JwtClaims(principalId, principalType, userId, merchantId, roles,
                    Collections.emptySet(), displayName, roleType);

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            return null;
        }
    }

    private Key buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (Exception e) {
            // 如果 secret 不是合法的 Base64，直接使用 UTF-8 字节作为 HMAC 密钥
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
