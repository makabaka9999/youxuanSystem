package com.youxuan.platform.security.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthAccount {

    private Long principalId;
    private PrincipalType principalType;
    private Long userId;
    private Long merchantId;
    private String username;
    private String displayName;
    private String passwordHash;
    private String status;
    private Set<String> roles = new HashSet<>();
    private Set<String> permissions = new HashSet<>();

    public Long getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    public PrincipalType getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(PrincipalType principalType) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles == null ? new HashSet<>() : new HashSet<>(roles);
    }

    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions == null ? new HashSet<>() : new HashSet<>(permissions);
    }
}
