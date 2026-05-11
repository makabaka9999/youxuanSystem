SET NAMES utf8mb4;

INSERT INTO roles (id, role_code, role_name, role_scope)
VALUES
  (1001, 'USER', '普通用户', 'USER'),
  (2001, 'MERCHANT_OWNER', '商家老板账号', 'MERCHANT'),
  (2002, 'MERCHANT_STAFF', '商家员工账号', 'MERCHANT'),
  (3001, 'PLATFORM_ADMIN', '平台管理员', 'PLATFORM'),
  (3002, 'PLATFORM_FINANCE', '平台财务', 'PLATFORM'),
  (3003, 'PLATFORM_AUDITOR', '平台审核员', 'PLATFORM')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), role_scope = VALUES(role_scope);

INSERT INTO permissions (id, permission_code, permission_name, permission_scope)
VALUES
  (100101, 'user:profile:read', '查看个人资料', 'USER'),
  (100102, 'user:cart:write', '维护购物车', 'USER'),
  (100103, 'user:order:write', '创建和维护个人订单', 'USER'),
  (200101, 'merchant:dashboard:read', '查看商家工作台', 'MERCHANT'),
  (200102, 'merchant:order:write', '处理商家订单', 'MERCHANT'),
  (200103, 'merchant:finance:read', '查看商家财务', 'MERCHANT'),
  (300101, 'admin:dashboard:read', '查看平台看板', 'PLATFORM'),
  (300102, 'admin:merchant:audit', '审核商家', 'PLATFORM'),
  (300103, 'admin:product:audit', '审核商品', 'PLATFORM'),
  (300104, 'admin:exception:write', '处理异常池', 'PLATFORM'),
  (300105, 'admin:settlement:audit', '审核结算单', 'PLATFORM'),
  (300106, 'admin:operation-log:read', '查询操作日志', 'PLATFORM')
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name), permission_scope = VALUES(permission_scope);
