package com.youxuan.auth.dto;

import java.util.Set;

/**
 * 当前登录主体信息 DTO。
 * <p>
 * 封装当前已认证用户的核心信息，包括主体标识、类型、关联用户/商户 ID、
 * 显示名称以及角色和权限集合等，用于权限校验和前端展示。
 * </p>
 */
public class CurrentPrincipalDTO {

    /** 主体 ID（账号记录主键） */
    private Long principalId;

    /** 主体类型（USER / MERCHANT_STAFF / PLATFORM_ADMIN） */
    private String principalType;

    /** 关联用户 ID */
    private Long userId;

    /** 关联商户 ID（商家员工类型时有效） */
    private Long merchantId;

    /** 登录账号 */
    private String account;

    /** 显示名称 */
    private String displayName;

    /** 商家员工角色类型（ADMIN / OPERATOR / CUSTOMER_SERVICE） */
    private String roleType;

    /** 角色编码集合 */
    private Set<String> roleCodeSet;

    /** 权限编码集合 */
    private Set<String> permissionCodeSet;

    /**
     * 获取主体 ID。
     *
     * @return 主体 ID
     */
    public Long getPrincipalId() {
        return principalId;
    }

    /**
     * 设置主体 ID。
     *
     * @param principalId 主体 ID
     */
    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    /**
     * 获取主体类型。
     *
     * @return 主体类型字符串
     */
    public String getPrincipalType() {
        return principalType;
    }

    /**
     * 设置主体类型。
     *
     * @param principalType 主体类型字符串
     */
    public void setPrincipalType(String principalType) {
        this.principalType = principalType;
    }

    /**
     * 获取关联用户 ID。
     *
     * @return 用户 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置关联用户 ID。
     *
     * @param userId 用户 ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取关联商户 ID。
     *
     * @return 商户 ID
     */
    public Long getMerchantId() {
        return merchantId;
    }

    /**
     * 设置关联商户 ID。
     *
     * @param merchantId 商户 ID
     */
    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * 获取登录账号。
     *
     * @return 登录账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置登录账号。
     *
     * @param account 登录账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取显示名称。
     *
     * @return 显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 设置显示名称。
     *
     * @param displayName 显示名称
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 获取商家员工角色类型。
     *
     * @return 角色类型
     */
    public String getRoleType() {
        return roleType;
    }

    /**
     * 设置商家员工角色类型。
     *
     * @param roleType 角色类型
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    /**
     * 获取角色编码集合。
     *
     * @return 角色编码集合
     */
    public Set<String> getRoleCodeSet() {
        return roleCodeSet;
    }

    /**
     * 设置角色编码集合。
     *
     * @param roleCodeSet 角色编码集合
     */
    public void setRoleCodeSet(Set<String> roleCodeSet) {
        this.roleCodeSet = roleCodeSet;
    }

    /**
     * 获取权限编码集合。
     *
     * @return 权限编码集合
     */
    public Set<String> getPermissionCodeSet() {
        return permissionCodeSet;
    }

    /**
     * 设置权限编码集合。
     *
     * @param permissionCodeSet 权限编码集合
     */
    public void setPermissionCodeSet(Set<String> permissionCodeSet) {
        this.permissionCodeSet = permissionCodeSet;
    }
}
