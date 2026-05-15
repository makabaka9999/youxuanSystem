# 商家员工管理

## 现状

- 后端 StaffController 已实现: 列表/创建/更新/切换状态
- 前端 MerchantPortal 已有员工管理 UI: 列表分页/搜索/添加/编辑/启用停用
- 创建员工时自动创建 user_roles 关联 (MERCHANT_STAFF 角色)
- 员工管理仅 ADMIN/OWNER 角色可操作 (后端 requireAdmin 校验)
- 添加员工通过手机号查找已有用户
- 角色可多选 (ADMIN/OPERATOR/CUSTOMER_SERVICE)

## 注册

- POST /api/v1/auth/register — 手机号注册，RSA加密密码
- 注册后自动分配 USER 角色
- 暂不需要短信验证码
