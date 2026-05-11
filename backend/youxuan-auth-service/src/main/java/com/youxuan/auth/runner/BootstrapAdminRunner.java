package com.youxuan.auth.runner;

import com.youxuan.auth.domain.PrincipalTypeEnum;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动后自动检测并创建默认超级管理员账号。
 * 超级管理员用户名：admin，密码：admin123，角色：PLATFORM_ADMIN。
 */
@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public BootstrapAdminRunner(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否已存在超级管理员
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM platform_admins WHERE username = ?",
                Integer.class, "admin");

        if (count != null && count > 0) {
            log.info("Super admin already exists, skip bootstrap.");
            return;
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String passwordHash = passwordEncoder.encode("admin123");

        // 插入平台管理员账号
        jdbcTemplate.update(
                "INSERT INTO platform_admins (id, username, password_hash, display_name, mobile, status, " +
                "last_login_at, remark, created_at, updated_at, version) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                100001L, "admin", passwordHash, "超级管理员", "13800000000",
                "ENABLED", now, "Bootstrap 默认超级管理员", now, now, 0);

        // 建立 platform_admins -> PLATFORM_ADMIN 角色关联
        jdbcTemplate.update(
                "INSERT IGNORE INTO user_roles (id, principal_type, principal_id, role_id, created_at, updated_at, version) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                900001L, PrincipalTypeEnum.PLATFORM_ADMIN.name(), 100001L, 3001L, now, now, 0);

        log.info("Super admin created: username=admin, password=admin123, role=PLATFORM_ADMIN");
    }
}
