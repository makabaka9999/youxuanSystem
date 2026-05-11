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

@Service
public class JwtTokenService {

    private final AuthSecurityProperties authSecurityProperties;
    private final Key signingKey;

    public JwtTokenService(AuthSecurityProperties authSecurityProperties) {
        this.authSecurityProperties = authSecurityProperties;
        this.signingKey = buildSigningKey(authSecurityProperties.getSecret());
    }

    public String createAccessToken(AuthAccountDO authAccountDO) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(authSecurityProperties.getAccessTokenTtlMinutes() * 60L);
        Map<String, Object> claims = new HashMap<>(8);
        claims.put("principalId", authAccountDO.getPrincipalId());
        claims.put("principalType", authAccountDO.getPrincipalType().name());
        claims.put("userId", authAccountDO.getUserId());
        claims.put("merchantId", authAccountDO.getMerchantId());
        claims.put("roles", authAccountDO.getRoleCodeSet());
        return Jwts.builder()
                .setIssuer(authSecurityProperties.getIssuer())
                .setSubject(authAccountDO.getAccount())
                .setClaims(claims)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiredAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private Key buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters");
        }
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException exception) {
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
