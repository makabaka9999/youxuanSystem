SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL COMMENT '用户ID',
  mobile VARCHAR(20) NOT NULL COMMENT '手机号',
  password_hash VARCHAR(255) NULL COMMENT '密码哈希',
  nickname VARCHAR(64) NULL COMMENT '昵称',
  avatar_url VARCHAR(500) NULL COMMENT '头像URL',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED, DISABLED',
  last_login_at DATETIME(3) NULL COMMENT '最后登录时间',
  remark VARCHAR(500) NULL COMMENT '备注',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_mobile (mobile),
  KEY idx_users_status (status),
  KEY idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS merchants (
  id BIGINT NOT NULL COMMENT '商家ID',
  merchant_no VARCHAR(64) NOT NULL COMMENT '商家编号',
  owner_user_id BIGINT NOT NULL COMMENT '老板用户ID',
  company_name VARCHAR(128) NOT NULL COMMENT '主体名称',
  license_no VARCHAR(64) NOT NULL COMMENT '营业执照号',
  contact_name VARCHAR(64) NOT NULL COMMENT '联系人',
  contact_mobile VARCHAR(20) NOT NULL COMMENT '联系电话',
  audit_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, APPROVED, REJECTED',
  status VARCHAR(32) NOT NULL DEFAULT 'DISABLED' COMMENT 'ENABLED, FROZEN, DISABLED',
  reject_reason VARCHAR(500) NULL COMMENT '驳回原因',
  approved_at DATETIME(3) NULL COMMENT '审核通过时间',
  remark VARCHAR(500) NULL COMMENT '备注',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_merchants_no (merchant_no),
  UNIQUE KEY uk_merchants_license (license_no),
  KEY idx_merchants_owner (owner_user_id),
  KEY idx_merchants_audit_status (audit_status),
  KEY idx_merchants_status (status),
  CONSTRAINT fk_merchants_owner_user FOREIGN KEY (owner_user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家表';

CREATE TABLE IF NOT EXISTS merchant_staffs (
  id BIGINT NOT NULL COMMENT '员工ID',
  merchant_id BIGINT NOT NULL COMMENT '商家ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  staff_name VARCHAR(64) NOT NULL COMMENT '员工姓名',
  role_type VARCHAR(32) NOT NULL COMMENT 'OWNER, STAFF',
  menu_permissions JSON NULL COMMENT '菜单权限编码列表',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED, DISABLED',
  last_login_at DATETIME(3) NULL COMMENT '最后登录时间',
  remark VARCHAR(500) NULL COMMENT '备注',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_merchant_staffs_user (merchant_id, user_id),
  KEY idx_merchant_staffs_user (user_id),
  KEY idx_merchant_staffs_status (merchant_id, status),
  CONSTRAINT fk_merchant_staffs_merchant FOREIGN KEY (merchant_id) REFERENCES merchants (id),
  CONSTRAINT fk_merchant_staffs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商家员工表';

CREATE TABLE IF NOT EXISTS platform_admins (
  id BIGINT NOT NULL COMMENT '平台账号ID',
  username VARCHAR(64) NOT NULL COMMENT '登录名',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  display_name VARCHAR(64) NOT NULL COMMENT '展示名',
  mobile VARCHAR(20) NULL COMMENT '手机号',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED, DISABLED',
  last_login_at DATETIME(3) NULL COMMENT '最后登录时间',
  remark VARCHAR(500) NULL COMMENT '备注',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_platform_admins_username (username),
  KEY idx_platform_admins_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='平台后台账号表';

CREATE TABLE IF NOT EXISTS roles (
  id BIGINT NOT NULL COMMENT '角色ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  role_scope VARCHAR(32) NOT NULL COMMENT 'USER, MERCHANT, PLATFORM',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED, DISABLED',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_code (role_code),
  KEY idx_roles_scope (role_scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

CREATE TABLE IF NOT EXISTS permissions (
  id BIGINT NOT NULL COMMENT '权限ID',
  permission_code VARCHAR(128) NOT NULL COMMENT '权限编码',
  permission_name VARCHAR(128) NOT NULL COMMENT '权限名称',
  permission_scope VARCHAR(32) NOT NULL COMMENT 'USER, MERCHANT, PLATFORM',
  resource_type VARCHAR(32) NOT NULL DEFAULT 'API' COMMENT 'API, MENU, ACTION',
  status VARCHAR(32) NOT NULL DEFAULT 'ENABLED' COMMENT 'ENABLED, DISABLED',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_permissions_code (permission_code),
  KEY idx_permissions_scope (permission_scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

CREATE TABLE IF NOT EXISTS role_permissions (
  id BIGINT NOT NULL COMMENT '角色权限ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_role_permissions (role_id, permission_id),
  KEY idx_role_permissions_permission (permission_id),
  CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles (id),
  CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关系表';

CREATE TABLE IF NOT EXISTS user_roles (
  id BIGINT NOT NULL COMMENT '用户角色ID',
  principal_type VARCHAR(32) NOT NULL COMMENT 'USER, MERCHANT_STAFF, PLATFORM_ADMIN',
  principal_id BIGINT NOT NULL COMMENT '主体ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_roles (principal_type, principal_id, role_id),
  KEY idx_user_roles_role (role_id),
  CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='主体角色关系表';

CREATE TABLE IF NOT EXISTS idempotent_records (
  id BIGINT NOT NULL COMMENT '幂等记录ID',
  idempotency_key VARCHAR(128) NOT NULL COMMENT '幂等Key',
  request_id VARCHAR(64) NOT NULL COMMENT '请求ID',
  request_hash VARCHAR(128) NOT NULL COMMENT '请求参数哈希',
  biz_type VARCHAR(64) NOT NULL COMMENT '业务类型',
  biz_id VARCHAR(64) NULL COMMENT '业务ID',
  status VARCHAR(32) NOT NULL DEFAULT 'PROCESSING' COMMENT 'PROCESSING, SUCCESS, FAILED',
  response_json JSON NULL COMMENT '成功响应快照',
  expired_at DATETIME(3) NOT NULL COMMENT '过期时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  UNIQUE KEY uk_idempotent_key (idempotency_key),
  KEY idx_idempotent_biz (biz_type, biz_id),
  KEY idx_idempotent_expired (expired_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='幂等记录表';

CREATE TABLE IF NOT EXISTS operation_logs (
  id BIGINT NOT NULL COMMENT '操作日志ID',
  operator_type VARCHAR(32) NOT NULL COMMENT 'USER, MERCHANT, PLATFORM, SYSTEM',
  operator_id BIGINT NULL COMMENT '操作人ID',
  operator_name VARCHAR(64) NULL COMMENT '操作人名称',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码',
  action_code VARCHAR(64) NOT NULL COMMENT '动作编码',
  target_type VARCHAR(64) NOT NULL COMMENT '对象类型',
  target_id VARCHAR(64) NOT NULL COMMENT '对象ID',
  request_id VARCHAR(64) NULL COMMENT '请求ID',
  request_ip VARCHAR(64) NULL COMMENT '请求IP',
  before_state JSON NULL COMMENT '操作前状态',
  after_state JSON NULL COMMENT '操作后状态',
  result VARCHAR(32) NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS, FAILED',
  remark VARCHAR(500) NULL COMMENT '备注',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL COMMENT '软删除时间',
  version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
  PRIMARY KEY (id),
  KEY idx_operation_logs_operator (operator_type, operator_id),
  KEY idx_operation_logs_module (module_code, action_code),
  KEY idx_operation_logs_target (target_type, target_id),
  KEY idx_operation_logs_request (request_id),
  KEY idx_operation_logs_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='后台操作日志表';
