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

INSERT INTO permissions (id, permission_code, permission_name, permission_scope, resource_type)
VALUES
  (100101, 'user:profile:read', '查看个人资料', 'USER', 'API'),
  (100102, 'user:cart:write', '维护购物车', 'USER', 'API'),
  (100103, 'user:order:write', '创建和维护个人订单', 'USER', 'API'),
  (100104, 'user:after-sale:write', '申请和维护个人售后', 'USER', 'API'),

  (200101, 'merchant:dashboard:read', '查看商家工作台', 'MERCHANT', 'API'),
  (200102, 'merchant:product:write', '维护商家商品', 'MERCHANT', 'API'),
  (200103, 'merchant:order:write', '处理商家订单', 'MERCHANT', 'API'),
  (200104, 'merchant:after-sale:write', '处理商家售后', 'MERCHANT', 'API'),
  (200105, 'merchant:finance:read', '查看商家财务', 'MERCHANT', 'API'),
  (200106, 'merchant:withdraw:write', '发起提现申请', 'MERCHANT', 'API'),
  (200107, 'merchant:staff:write', '维护员工账号', 'MERCHANT', 'API'),

  (300101, 'admin:dashboard:read', '查看平台看板', 'PLATFORM', 'API'),
  (300102, 'admin:merchant:audit', '审核商家', 'PLATFORM', 'API'),
  (300103, 'admin:product:audit', '审核商品', 'PLATFORM', 'API'),
  (300104, 'admin:exception:write', '处理异常池', 'PLATFORM', 'API'),
  (300105, 'admin:settlement:audit', '审核结算单', 'PLATFORM', 'API'),
  (300106, 'admin:withdraw:audit', '审核提现单', 'PLATFORM', 'API'),
  (300107, 'admin:operation-log:read', '查询操作日志', 'PLATFORM', 'API'),
  (300108, 'admin:system-config:write', '维护系统配置', 'PLATFORM', 'API')
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name), permission_scope = VALUES(permission_scope), resource_type = VALUES(resource_type);

INSERT INTO role_permissions (id, role_id, permission_id)
SELECT 400000 + r.id + p.id, r.id, p.id
FROM roles r
JOIN permissions p ON (
  (r.role_code = 'USER' AND p.permission_scope = 'USER')
  OR (r.role_code = 'MERCHANT_OWNER' AND p.permission_scope = 'MERCHANT')
  OR (r.role_code = 'MERCHANT_STAFF' AND p.permission_code IN ('merchant:dashboard:read','merchant:product:write','merchant:order:write','merchant:after-sale:write'))
  OR (r.role_code = 'PLATFORM_ADMIN' AND p.permission_scope = 'PLATFORM')
  OR (r.role_code = 'PLATFORM_FINANCE' AND p.permission_code IN ('admin:dashboard:read','admin:settlement:audit','admin:withdraw:audit','admin:operation-log:read'))
  OR (r.role_code = 'PLATFORM_AUDITOR' AND p.permission_code IN ('admin:dashboard:read','admin:merchant:audit','admin:product:audit','admin:operation-log:read'))
)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP(3);
