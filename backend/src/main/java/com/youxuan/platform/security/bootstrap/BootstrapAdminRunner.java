package com.youxuan.platform.security.bootstrap;

import com.youxuan.platform.common.id.IdGenerator;
import com.youxuan.platform.security.config.SecurityProperties;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BootstrapAdminRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminRunner.class);
    private static final long PLATFORM_ADMIN_ROLE_ID = 3001L;

    private final SecurityProperties securityProperties;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final IdGenerator idGenerator;

    public BootstrapAdminRunner(SecurityProperties securityProperties,
                                JdbcTemplate jdbcTemplate,
                                PasswordEncoder passwordEncoder,
                                IdGenerator idGenerator) {
        this.securityProperties = securityProperties;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.idGenerator = idGenerator;
    }

    @Override
    public void run(ApplicationArguments args) {
        SecurityProperties.BootstrapAdmin bootstrap = securityProperties.getBootstrapAdmin();
        if (!bootstrap.isEnabled()) {
            return;
        }
        if (!StringUtils.hasText(bootstrap.getPassword()) || bootstrap.getPassword().length() < 12) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD must be at least 12 characters when bootstrap is enabled");
        }
        List<Long> existingIds = jdbcTemplate.queryForList(
                "SELECT id FROM platform_admins WHERE username = ? AND deleted_at IS NULL",
                Long.class,
                bootstrap.getUsername());
        Long adminId;
        if (existingIds.isEmpty()) {
            adminId = idGenerator.nextId();
            jdbcTemplate.update(
                    "INSERT INTO platform_admins (id, username, password_hash, display_name, status) VALUES (?, ?, ?, ?, 'ENABLED')",
                    adminId,
                    bootstrap.getUsername(),
                    passwordEncoder.encode(bootstrap.getPassword()),
                    bootstrap.getDisplayName());
            log.warn("Bootstrap platform admin created. Disable BOOTSTRAP_ADMIN_ENABLED after first login.");
        } else {
            adminId = existingIds.get(0);
            log.info("Bootstrap platform admin already exists, skip creating account.");
        }
        Integer roleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_roles WHERE principal_type = 'PLATFORM_ADMIN' AND principal_id = ? AND role_id = ? AND deleted_at IS NULL",
                Integer.class,
                adminId,
                PLATFORM_ADMIN_ROLE_ID);
        if (roleCount == null || roleCount == 0) {
            jdbcTemplate.update(
                    "INSERT INTO user_roles (id, principal_type, principal_id, role_id) VALUES (?, 'PLATFORM_ADMIN', ?, ?)",
                    idGenerator.nextId(),
                    adminId,
                    PLATFORM_ADMIN_ROLE_ID);
        }
    }
}
