-- ============================================================
-- V3: 修复已有数据库中缺失的列（兼容 Flyway repair 场景）
--
-- 适用场景：已有数据库在 V1 建表时缺少以下列，
-- 通过 Flyway repair + V3 可以不用 drop database 直接修复。
-- ============================================================

SET NAMES utf8mb4;

-- 用存储过程安全添加列（列已存在时跳过，不报错）
DROP PROCEDURE IF EXISTS add_col_if_missing;
DELIMITER //
CREATE PROCEDURE add_col_if_missing(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_definition TEXT
)
BEGIN
    DECLARE col_count INT;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_table AND COLUMN_NAME = p_column;
    IF col_count = 0 THEN
        SET @ddl = CONCAT('ALTER TABLE ', p_table, ' ADD COLUMN ', p_column, ' ', p_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

-- permissions 表：补 resource_type 列
CALL add_col_if_missing('permissions', 'resource_type',
    'VARCHAR(32) NULL DEFAULT ''API'' COMMENT ''资源类型(API, MENU, BUTTON)'' AFTER permission_scope');

-- platform_admins 表：补 mobile / last_login_at / remark 列
CALL add_col_if_missing('platform_admins', 'mobile',
    'VARCHAR(20) NULL COMMENT ''手机号'' AFTER display_name');

CALL add_col_if_missing('platform_admins', 'last_login_at',
    'DATETIME(3) NULL COMMENT ''最后登录时间'' AFTER status');

CALL add_col_if_missing('platform_admins', 'remark',
    'VARCHAR(255) NULL COMMENT ''备注'' AFTER last_login_at');

-- 清理存储过程
DROP PROCEDURE IF EXISTS add_col_if_missing;
