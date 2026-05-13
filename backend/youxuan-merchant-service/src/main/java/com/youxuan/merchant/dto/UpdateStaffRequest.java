package com.youxuan.merchant.dto;

/**
 * 更新员工信息请求 DTO。
 */
public class UpdateStaffRequest {

    /** 员工姓名 */
    private String staffName;

    /** 角色类型（多选用逗号分隔） */
    private String roleType;

    /** 菜单权限（JSON 字符串） */
    private String menuPermissions;

    /** 备注 */
    private String remark;

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    public String getMenuPermissions() { return menuPermissions; }
    public void setMenuPermissions(String menuPermissions) { this.menuPermissions = menuPermissions; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
