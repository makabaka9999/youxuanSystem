package com.youxuan.platform.auth.dto;

import java.util.Set;

public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresInSeconds;
    private CurrentUserResponse currentUser;

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

    public CurrentUserResponse getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUserResponse currentUser) {
        this.currentUser = currentUser;
    }

    public static class CurrentUserResponse {
        private Long principalId;
        private String principalType;
        private Long userId;
        private Long merchantId;
        private String username;
        private String displayName;
        private Set<String> roles;
        private Set<String> permissions;

        public Long getPrincipalId() {
            return principalId;
        }

        public void setPrincipalId(Long principalId) {
            this.principalId = principalId;
        }

        public String getPrincipalType() {
            return principalType;
        }

        public void setPrincipalType(String principalType) {
            this.principalType = principalType;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(Long merchantId) {
            this.merchantId = merchantId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Set<String> getRoles() {
            return roles;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }

        public Set<String> getPermissions() {
            return permissions;
        }

        public void setPermissions(Set<String> permissions) {
            this.permissions = permissions;
        }
    }
}
