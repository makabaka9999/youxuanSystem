package com.youxuan.auth.model;

import com.youxuan.auth.domain.PrincipalTypeEnum;
import java.util.HashSet;
import java.util.Set;

public class AuthAccountDO {

    private Long principalId;
    private PrincipalTypeEnum principalType;
    private Long userId;
    private Long merchantId;
    private String account;
    private String passwordHash;
    private String displayName;
    private String status;
    private Set<String> roleCodeSet = new HashSet<>();
    private Set<String> permissionCodeSet = new HashSet<>();

    public Long getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    public PrincipalTypeEnum getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(PrincipalTypeEnum principalType) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
