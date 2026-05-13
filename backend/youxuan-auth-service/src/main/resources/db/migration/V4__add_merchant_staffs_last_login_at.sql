-- ============================================================
-- merchant_staffs 表补充 last_login_at 字段
-- ============================================================

ALTER TABLE merchant_staffs
  ADD COLUMN last_login_at DATETIME(3) NULL DEFAULT NULL COMMENT '最后登录时间' AFTER status;
