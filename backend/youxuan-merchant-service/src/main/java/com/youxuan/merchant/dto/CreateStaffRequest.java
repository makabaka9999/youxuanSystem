package com.youxuan.merchant.dto;

/**
 * 创建商家员工请求 DTO。
 */
public class CreateStaffRequest {

    /** 用户 ID */
    private Long userId;

    /** 员工姓名 */
    private String staffName;

    /** 角色类型：ADMIN / OPERATOR / CUSTOMER_SERVICE */
    private String roleType;

    /** 菜单权限（JSON 字符串） */
    private String menuPermissions;

    /** 备注 */
    private String remark;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }
    public String getMenuPermissions() { return menuPermissions; }
    public void setMenuPermissions(String menuPermissions) { this.menuPermissions = menuPermissions; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
