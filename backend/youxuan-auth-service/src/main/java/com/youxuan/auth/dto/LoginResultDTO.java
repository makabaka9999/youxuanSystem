package com.youxuan.auth.dto;

/**
 * 登录结果 DTO。
 * <p>
 * 封装登录成功后的返回数据，包含 JWT 访问令牌、令牌类型、
 * 有效期以及当前登录主体的详细信息。
 * </p>
 */
public class LoginResultDTO {

    /** JWT 访问令牌字符串 */
    private String accessToken;

    /** 令牌类型（固定为 Bearer） */
    private String tokenType;

    /** 令牌过期时间（秒） */
    private Long expiresInSeconds;

    /** 当前登录主体详细信息 */
    private CurrentPrincipalDTO currentPrincipal;

    /**
     * 获取 JWT 访问令牌。
     *
     * @return JWT 令牌字符串
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * 设置 JWT 访问令牌。
     *
     * @param accessToken JWT 令牌字符串
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * 获取令牌类型。
     *
     * @return 令牌类型（如 Bearer）
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * 设置令牌类型。
     *
     * @param tokenType 令牌类型
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * 获取令牌过期时间（秒）。
     *
     * @return 过期秒数
     */
    public Long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    /**
     * 设置令牌过期时间（秒）。
     *
     * @param expiresInSeconds 过期秒数
     */
    public void setExpiresInSeconds(Long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    /**
     * 获取当前登录主体信息。
     *
     * @return 当前主体详细信息
     */
    public CurrentPrincipalDTO getCurrentPrincipal() {
        return currentPrincipal;
    }

    /**
     * 设置当前登录主体信息。
     *
     * @param currentPrincipal 当前主体详细信息
     */
    public void setCurrentPrincipal(CurrentPrincipalDTO currentPrincipal) {
        this.currentPrincipal = currentPrincipal;
    }
}
