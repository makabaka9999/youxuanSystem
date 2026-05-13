package com.youxuan.auth.model;

import com.youxuan.auth.domain.PrincipalTypeEnum;
import java.util.HashSet;
import java.util.Set;

/**
 * 认证账号数据对象（DO）。
 * <p>
 * 封装从数据库查询到的账号信息，包含主体标识、认证凭据（密码哈希）、
 * 显示名称、状态以及关联的角色和权限集合。
 * 该对象在数据访问层与应用服务层之间传递。
 * </p>
 */
public class AuthAccountDO {

    /** 主体 ID（账号记录主键） */
    private Long principalId;

    /** 主体类型枚举 */
    private PrincipalTypeEnum principalType;

    /** 关联用户 ID */
    private Long userId;

    /** 关联商户 ID */
    private Long merchantId;

    /** 登录账号 */
    private String account;

    /** 密码哈希值 */
    private String passwordHash;

    /** 显示名称 */
    private String displayName;

    /** 账号状态（ENABLED / DISABLED 等） */
    private String status;

    /** 商家员工角色类型（ADMIN / OPERATOR / CUSTOMER_SERVICE，仅 MERCHANT_STAFF 有效） */
    private String roleType;

    /** 角色编码集合 */
    private Set<String> roleCodeSet = new HashSet<>();

    /** 权限编码集合 */
    private Set<String> permissionCodeSet = new HashSet<>();

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
     * 获取主体类型枚举。
     *
     * @return 主体类型枚举
     */
    public PrincipalTypeEnum getPrincipalType() {
        return principalType;
    }

    /**
     * 设置主体类型枚举。
     *
     * @param principalType 主体类型枚举
     */
    public void setPrincipalType(PrincipalTypeEnum principalType) {
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
     * 获取密码哈希值。
     *
     * @return 密码哈希值
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * 设置密码哈希值。
     *
     * @param passwordHash 密码哈希值
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
     * 获取账号状态。
     *
     * @return 账号状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置账号状态。
     *
     * @param status 账号状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取商家员工角色类型。
     *
     * @return 角色类型（ADMIN / OPERATOR / CUSTOMER_SERVICE）
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
