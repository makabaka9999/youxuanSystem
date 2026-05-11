package com.youxuan.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youxuan.auth.security")
public class AuthSecurityProperties {

    private String issuer = "youxuan-auth-service";
    private String secret = "replace-with-at-least-32-bytes-secret";
    private long accessTokenTtlMinutes = 120L;
    private int passwordStrength = 12;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenTtlMinutes() {
        return accessTokenTtlMinutes;
    }

    public void setAccessTokenTtlMinutes(long accessTokenTtlMinutes) {
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    public int getPasswordStrength() {
        return passwordStrength;
    }

    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }
}
