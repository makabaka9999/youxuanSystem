# 认证与 RBAC 权限

## 登录类型 (principalType)

- **USER** — C端消费者，查 users 表
- **MERCHANT_STAFF** — 商家员工，联表 users + merchant_staffs
- **PLATFORM_ADMIN** — 平台管理员，查 platform_admins 表

## 认证流程

1. 前端 → GET /api/v1/auth/public-key 获取 RSA-2048 公钥
2. 前端 Web Crypto API 加密密码 (RSA-OAEP/SHA-256)
3. 前端 → POST /api/v1/auth/password-login
4. 后端 RSA 解密 → BCrypt 比对 (strength=12)
5. 返回 JWT (HS256) + currentPrincipal

## 商户员工角色类型 (merchant_staffs.role_type)

- **OWNER** — 老板，全部权限
- **ADMIN** — 管理员，全部权限
- **OPERATOR** — 运营，工作台/商品/订单/售后
- **CUSTOMER_SERVICE** — 客服，工作台/售后

## RBAC 角色

- USER(1001), MERCHANT_OWNER(2001), MERCHANT_STAFF(2002)
- PLATFORM_ADMIN(3001), PLATFORM_FINANCE(3002), PLATFORM_AUDITOR(3003)
