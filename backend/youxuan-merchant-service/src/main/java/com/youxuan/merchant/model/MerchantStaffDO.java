package com.youxuan.merchant.model;

import java.time.LocalDateTime;

/**
 * 商家员工数据对象，对应 merchant_staffs 表。
 */
public class MerchantStaffDO {

    /** 员工 ID */
    private Long id;

    /** 商家 ID */
    private Long merchantId;

    /** 用户 ID（关联 users 表） */
    private Long userId;

    /** 员工姓名 */
    private String staffName;

    /** 角色类型：ADMIN / OPERATOR / CUSTOMER_SERVICE */
    private String roleType;

    /** 菜单权限（JSON 字符串，存储有权限的菜单编码列表） */
    private String menuPermissions;

    /** 状态：ENABLED / DISABLED */
    private String status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }
    public String getMenuPermissions() { return menuPermissions; }
    public void setMenuPermissions(String menuPermissions) { this.menuPermissions = menuPermissions; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
