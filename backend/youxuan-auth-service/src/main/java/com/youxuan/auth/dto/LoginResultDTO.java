package com.youxuan.auth.dto;

public class LoginResultDTO {

    private String accessToken;
    private String tokenType;
    private Long expiresInSeconds;
    private CurrentPrincipalDTO currentPrincipal;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(Long expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }

    public CurrentPrincipalDTO getCurrentPrincipal() {
        return currentPrincipal;
    }

    public void setCurrentPrincipal(CurrentPrincipalDTO currentPrincipal) {
        this.currentPrincipal = currentPrincipal;
    }
}
