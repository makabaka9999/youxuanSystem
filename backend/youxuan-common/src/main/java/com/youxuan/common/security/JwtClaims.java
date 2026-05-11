package com.youxuan.common.security;

import java.util.Collections;
import java.util.Set;

/**
 * JWT 解析后的认证信息，包含主体身份、角色和权限。
 */
public class JwtClaims {

    /** 主体 ID（user / merchant_staff / platform_admin 的主键） */
    private Long principalId;

    /** 主体类型：USER / MERCHANT_STAFF / PLATFORM_ADMIN */
    private String principalType;

    /** 用户 ID（users 表主键）*/
    private Long userId;

    /** 商家 ID（可为 null）*/
    private Long merchantId;

    /** 角色编码集合 */
    private Set<String> roles = Collections.emptySet();

    /** 权限编码集合 */
    private Set<String> permissions = Collections.emptySet();

    /** 显示名称 */
    private String displayName;

    public JwtClaims() {}

    public JwtClaims(Long principalId, String principalType, Long userId, Long merchantId,
                     Set<String> roles, Set<String> permissions, String displayName) {
        this.principalId = principalId;
        this.principalType = principalType;
        this.userId = userId;
        this.merchantId = merchantId;
        this.roles = roles != null ? roles : Collections.emptySet();
        this.permissions = permissions != null ? permissions : Collections.emptySet();
        this.displayName = displayName;
    }

    public Long getPrincipalId() { return principalId; }
    public void setPrincipalId(Long principalId) { this.principalId = principalId; }

    public String getPrincipalType() { return principalType; }
    public void setPrincipalType(String principalType) { this.principalType = principalType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
}
