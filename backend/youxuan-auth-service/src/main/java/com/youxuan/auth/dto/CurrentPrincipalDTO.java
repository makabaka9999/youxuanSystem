package com.youxuan.auth.dto;

import java.util.Set;

public class CurrentPrincipalDTO {

    private Long principalId;
    private String principalType;
    private Long userId;
    private Long merchantId;
    private String account;
    private String displayName;
    private Set<String> roleCodeSet;
    private Set<String> permissionCodeSet;

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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<String> getRoleCodeSet() {
        return roleCodeSet;
    }

    public void setRoleCodeSet(Set<String> roleCodeSet) {
        this.roleCodeSet = roleCodeSet;
    }

    public Set<String> getPermissionCodeSet() {
        return permissionCodeSet;
    }

    public void setPermissionCodeSet(Set<String> permissionCodeSet) {
        this.permissionCodeSet = permissionCodeSet;
    }
}
