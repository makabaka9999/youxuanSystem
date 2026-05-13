-- ============================================================
-- 种子数据：普通消费用户 + 商家用户
-- 密码统一为 123456（BCrypt 强度 12）
-- ============================================================

SET NAMES utf8mb4;

-- 密码 123456 的 BCrypt 哈希
SET @password_hash = '$2a$12$WT.cjeTflfv8PmwZwQvjiOQVvcX/17qOGvZwSKvLGuWLnYaO0YZi.';

-- ============================================================
-- 0. 补充已有测试用户的密码（方便登录测试）
--    手机号 13800000001 / 昵称 优选自营
-- ============================================================
UPDATE users SET password_hash = @password_hash, updated_at = NOW()
WHERE id = 100001 AND password_hash IS NULL;

-- ============================================================
-- 1. 普通消费用户
--    手机号：13800138002
--    登录类型：USER
--    密码：123456
--    角色：普通用户 (USER, role_id=1001)
-- ============================================================
INSERT INTO users (id, mobile, password_hash, nickname, status, created_at, updated_at, version)
VALUES (100002, '13800138002', @password_hash, '王小明', 'ENABLED', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), nickname = VALUES(nickname), updated_at = NOW();

-- 关联角色
INSERT IGNORE INTO user_roles (id, principal_type, principal_id, role_id, created_at, updated_at, version)
VALUES (10000201, 'USER', 100002, 1001, NOW(), NOW(), 0);

-- ============================================================
-- 2. 商家用户（商家老板）
--    手机号：13900139001
--    登录类型：MERCHANT_STAFF（登录时传此值）
--    密码：123456
--    角色：商家老板 (MERCHANT_OWNER, role_id=2001)
-- ============================================================
INSERT INTO users (id, mobile, password_hash, nickname, status, created_at, updated_at, version)
VALUES (100003, '13900139001', @password_hash, '李老板', 'ENABLED', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), nickname = VALUES(nickname), updated_at = NOW();

-- 创建商家主体（merchant_applications 表）
INSERT INTO merchant_applications (id, merchant_no, owner_user_id, company_name, license_no, contact_name, contact_mobile, audit_status, status, created_at, updated_at, version)
VALUES (80002, 'M202605120001', 100003, '优选数码科技有限公司', '91330100MA00000002', '李老板', '13900139001', 'APPROVED', 'ENABLED', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE company_name = VALUES(company_name), updated_at = NOW();

-- 创建店铺
INSERT INTO stores (id, merchant_id, store_no, store_name, status, created_at, updated_at, version)
VALUES (90002, 80002, 'S202605120001', '优选数码旗舰店', 'ENABLED', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE store_name = VALUES(store_name), updated_at = NOW();

-- 创建商家员工记录（老板本人）
INSERT INTO merchant_staffs (id, merchant_id, user_id, staff_name, role_type, status, created_at, updated_at, version)
VALUES (70001, 80002, 100003, '李老板', 'OWNER', 'ENABLED', NOW(), NOW(), 0)
ON DUPLICATE KEY UPDATE staff_name = VALUES(staff_name), updated_at = NOW();

-- 关联角色（principal_type 填 'USER'，因仓库代码对非 PLATFORM_ADMIN 统一查 users 表）
INSERT IGNORE INTO user_roles (id, principal_type, principal_id, role_id, created_at, updated_at, version)
VALUES (10000301, 'USER', 100003, 2001, NOW(), NOW(), 0);
