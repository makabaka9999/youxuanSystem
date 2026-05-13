-- ============================================================
-- 创建商家员工表 merchant_staffs（含 last_login_at 字段）
-- ============================================================

CREATE TABLE IF NOT EXISTS merchant_staffs (
  id BIGINT NOT NULL PRIMARY KEY,
  merchant_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  staff_name VARCHAR(64) NOT NULL,
  role_type VARCHAR(32) NOT NULL,
  menu_permissions TEXT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED',
  last_login_at DATETIME(3) NULL DEFAULT NULL COMMENT '最后登录时间',
  remark VARCHAR(512) NULL,
  deleted_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  version INT NOT NULL DEFAULT 0,
  KEY idx_merchant_staffs_merchant (merchant_id),
  KEY idx_merchant_staffs_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
