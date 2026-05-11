package com.youxuan.platform.security.repository;

import com.youxuan.platform.security.domain.AuthAccount;
import com.youxuan.platform.security.domain.PrincipalType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuthAccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AuthAccount> findUserByMobile(String mobile) {
        List<AuthAccount> accounts = jdbcTemplate.query(
                "SELECT id, mobile, password_hash, nickname, status FROM users WHERE mobile = ? AND deleted_at IS NULL",
                (rs, rowNum) -> mapUser(rs),
                mobile);
        return accounts.stream().findFirst().map(this::attachAuthorities);
    }

    public Optional<AuthAccount> findPlatformAdminByUsername(String username) {
        List<AuthAccount> accounts = jdbcTemplate.query(
                "SELECT id, username, password_hash, display_name, status FROM platform_admins WHERE username = ? AND deleted_at IS NULL",
                (rs, rowNum) -> mapPlatformAdmin(rs),
                username);
        return accounts.stream().findFirst().map(this::attachAuthorities);
    }

    public Optional<AuthAccount> findByPrincipal(PrincipalType principalType, Long principalId) {
        if (principalType == PrincipalType.PLATFORM_ADMIN) {
            List<AuthAccount> accounts = jdbcTemplate.query(
                    "SELECT id, username, password_hash, display_name, status FROM platform_admins WHERE id = ? AND deleted_at IS NULL",
                    (rs, rowNum) -> mapPlatformAdmin(rs),
                    principalId);
            return accounts.stream().findFirst().map(this::attachAuthorities);
        }
        if (principalType == PrincipalType.MERCHANT_STAFF) {
            List<AuthAccount> accounts = jdbcTemplate.query(
                    "SELECT ms.id, ms.merchant_id, ms.user_id, u.mobile, u.password_hash, ms.staff_name, ms.status " +
                            "FROM merchant_staffs ms JOIN users u ON u.id = ms.user_id " +
                            "WHERE ms.id = ? AND ms.deleted_at IS NULL AND u.deleted_at IS NULL",
                    (rs, rowNum) -> mapMerchantStaff(rs),
                    principalId);
            return accounts.stream().findFirst().map(this::attachAuthorities);
        }
        List<AuthAccount> accounts = jdbcTemplate.query(
                "SELECT id, mobile, password_hash, nickname, status FROM users WHERE id = ? AND deleted_at IS NULL",
                (rs, rowNum) -> mapUser(rs),
                principalId);
        return accounts.stream().findFirst().map(this::attachAuthorities);
    }

    private AuthAccount attachAuthorities(AuthAccount account) {
        Set<String> roles = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT r.role_code FROM user_roles ur JOIN roles r ON r.id = ur.role_id " +
                        "WHERE ur.principal_type = ? AND ur.principal_id = ? AND ur.deleted_at IS NULL AND r.deleted_at IS NULL AND r.status = 'ENABLED'",
                String.class,
                account.getPrincipalType().name(),
                account.getPrincipalId()));
        Set<String> permissions = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT p.permission_code FROM user_roles ur " +
                        "JOIN role_permissions rp ON rp.role_id = ur.role_id AND rp.deleted_at IS NULL " +
                        "JOIN permissions p ON p.id = rp.permission_id AND p.deleted_at IS NULL AND p.status = 'ENABLED' " +
                        "WHERE ur.principal_type = ? AND ur.principal_id = ? AND ur.deleted_at IS NULL",
                String.class,
                account.getPrincipalType().name(),
                account.getPrincipalId()));
        account.setRoles(roles);
        account.setPermissions(permissions);
        return account;
    }

    private AuthAccount mapUser(ResultSet rs) throws SQLException {
        AuthAccount account = new AuthAccount();
        account.setPrincipalId(rs.getLong("id"));
        account.setPrincipalType(PrincipalType.USER);
        account.setUserId(rs.getLong("id"));
        account.setUsername(rs.getString("mobile"));
        account.setPasswordHash(rs.getString("password_hash"));
        account.setDisplayName(rs.getString("nickname"));
        account.setStatus(rs.getString("status"));
        return account;
    }

    private AuthAccount mapPlatformAdmin(ResultSet rs) throws SQLException {
        AuthAccount account = new AuthAccount();
        account.setPrincipalId(rs.getLong("id"));
        account.setPrincipalType(PrincipalType.PLATFORM_ADMIN);
        account.setUsername(rs.getString("username"));
        account.setPasswordHash(rs.getString("password_hash"));
        account.setDisplayName(rs.getString("display_name"));
        account.setStatus(rs.getString("status"));
        return account;
    }

    private AuthAccount mapMerchantStaff(ResultSet rs) throws SQLException {
        AuthAccount account = new AuthAccount();
        account.setPrincipalId(rs.getLong("id"));
        account.setPrincipalType(PrincipalType.MERCHANT_STAFF);
        account.setUserId(rs.getLong("user_id"));
        account.setMerchantId(rs.getLong("merchant_id"));
        account.setUsername(rs.getString("mobile"));
        account.setPasswordHash(rs.getString("password_hash"));
        account.setDisplayName(rs.getString("staff_name"));
        account.setStatus(rs.getString("status"));
        return account;
    }
}
