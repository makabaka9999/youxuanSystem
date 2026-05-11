package com.youxuan.platform.security.jwt;

import com.youxuan.platform.security.config.SecurityProperties;
import com.youxuan.platform.security.domain.AuthPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

    private final SecurityProperties securityProperties;
    private final Key signingKey;

    public JwtTokenProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.signingKey = buildSigningKey(securityProperties.getJwt().getSecret());
    }

    public String createAccessToken(AuthPrincipal principal) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(securityProperties.getJwt().getAccessTokenTtlMinutes() * 60);
        Map<String, Object> claims = new HashMap<>();
        claims.put("principalType", principal.getPrincipalType().name());
        claims.put("principalId", principal.getPrincipalId());
        claims.put("userId", principal.getUserId());
        claims.put("merchantId", principal.getMerchantId());
        claims.put("roles", principal.getRoles());
        return Jwts.builder()
                .setIssuer(securityProperties.getJwt().getIssuer())
                .setSubject(principal.getUsername())
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .requireIssuer(securityProperties.getJwt().getIssuer())
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidJwtException("Token 无效或已过期", ex);
        }
    }

    private Key buildSigningKey(String secret) {
        if (!StringUtils.hasText(secret) || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
        }
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (IllegalArgumentException ex) {
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }
}
