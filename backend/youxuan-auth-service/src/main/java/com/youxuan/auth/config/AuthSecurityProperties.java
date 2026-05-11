package com.youxuan.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 认证安全配置属性类。
 * <p>
 * 从配置前缀 {@code youxuan.auth.security} 加载认证相关的安全配置参数，
 * 包括 JWT 签发者、密钥、令牌有效期及密码强度要求等。
 * </p>
 */
@ConfigurationProperties(prefix = "youxuan.auth.security")
public class AuthSecurityProperties {

    /** JWT 签发者名称，默认为 youxuan-auth-service */
    private String issuer = "youxuan-auth-service";
    /** JWT 签名密钥，至少 32 个字符 */
    private String secret = "replace-with-at-least-32-bytes-secret";
    /** 访问令牌有效期（分钟），默认 120 分钟 */
    private long accessTokenTtlMinutes = 120L;
    /** 密码强度要求（最小长度），默认 12 位 */
    private int passwordStrength = 12;

    /**
     * 获取 JWT 签发者名称。
     *
     * @return 签发者名称
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * 设置 JWT 签发者名称。
     *
     * @param issuer 签发者名称
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * 获取 JWT 签名密钥。
     *
     * @return 签名密钥字符串
     */
    public String getSecret() {
        return secret;
    }

    /**
     * 设置 JWT 签名密钥。
     *
     * @param secret 签名密钥字符串
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * 获取访问令牌有效期（分钟）。
     *
     * @return 有效期分钟数
     */
    public long getAccessTokenTtlMinutes() {
        return accessTokenTtlMinutes;
    }

    /**
     * 设置访问令牌有效期（分钟）。
     *
     * @param accessTokenTtlMinutes 有效期分钟数
     */
    public void setAccessTokenTtlMinutes(long accessTokenTtlMinutes) {
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

    /**
     * 获取密码强度要求（最小长度）。
     *
     * @return 密码最小长度
     */
    public int getPasswordStrength() {
        return passwordStrength;
    }

    /**
     * 设置密码强度要求（最小长度）。
     *
     * @param passwordStrength 密码最小长度
     */
    public void setPasswordStrength(int passwordStrength) {
        this.passwordStrength = passwordStrength;
    }
}
