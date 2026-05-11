package com.youxuan.auth.repository;

import com.youxuan.auth.domain.PrincipalTypeEnum;
import com.youxuan.auth.model.AuthAccountDO;
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

    public Optional<AuthAccountDO> findByAccount(PrincipalTypeEnum principalTypeEnum, String account) {
        if (principalTypeEnum == PrincipalTypeEnum.PLATFORM_ADMIN) {
            return findPlatformAdmin(account);
        }
        return findUser(account);
    }

    private Optional<AuthAccountDO> findUser(String mobile) {
        List<AuthAccountDO> accountList = jdbcTemplate.query(
                "SELECT id, mobile, password_hash, nickname, status FROM users WHERE mobile = ? AND deleted_at IS NULL",
                (resultSet, rowNum) -> mapUser(resultSet),
                mobile);
        return accountList.stream().findFirst().map(this::fillAuthority);
    }

    private Optional<AuthAccountDO> findPlatformAdmin(String username) {
        List<AuthAccountDO> accountList = jdbcTemplate.query(
                "SELECT id, username, password_hash, display_name, status FROM platform_admins WHERE username = ? AND deleted_at IS NULL",
                (resultSet, rowNum) -> mapPlatformAdmin(resultSet),
                username);
        return accountList.stream().findFirst().map(this::fillAuthority);
    }

    private AuthAccountDO fillAuthority(AuthAccountDO authAccountDO) {
        Set<String> roleCodeSet = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT r.role_code FROM user_roles ur JOIN roles r ON r.id = ur.role_id " +
                        "WHERE ur.principal_type = ? AND ur.principal_id = ? AND ur.deleted_at IS NULL AND r.deleted_at IS NULL",
                String.class,
                authAccountDO.getPrincipalType().name(),
                authAccountDO.getPrincipalId()));
        Set<String> permissionCodeSet = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT p.permission_code FROM user_roles ur " +
                        "JOIN role_permissions rp ON rp.role_id = ur.role_id AND rp.deleted_at IS NULL " +
                        "JOIN permissions p ON p.id = rp.permission_id AND p.deleted_at IS NULL " +
                        "WHERE ur.principal_type = ? AND ur.principal_id = ? AND ur.deleted_at IS NULL",
                String.class,
                authAccountDO.getPrincipalType().name(),
                authAccountDO.getPrincipalId()));
        authAccountDO.setRoleCodeSet(roleCodeSet);
        authAccountDO.setPermissionCodeSet(permissionCodeSet);
        return authAccountDO;
    }

    private AuthAccountDO mapUser(ResultSet resultSet) throws SQLException {
        AuthAccountDO authAccountDO = new AuthAccountDO();
        authAccountDO.setPrincipalId(resultSet.getLong("id"));
        authAccountDO.setPrincipalType(PrincipalTypeEnum.USER);
        authAccountDO.setUserId(resultSet.getLong("id"));
        authAccountDO.setAccount(resultSet.getString("mobile"));
        authAccountDO.setPasswordHash(resultSet.getString("password_hash"));
        authAccountDO.setDisplayName(resultSet.getString("nickname"));
        authAccountDO.setStatus(resultSet.getString("status"));
        return authAccountDO;
    }

    private AuthAccountDO mapPlatformAdmin(ResultSet resultSet) throws SQLException {
        AuthAccountDO authAccountDO = new AuthAccountDO();
        authAccountDO.setPrincipalId(resultSet.getLong("id"));
        authAccountDO.setPrincipalType(PrincipalTypeEnum.PLATFORM_ADMIN);
        authAccountDO.setAccount(resultSet.getString("username"));
        authAccountDO.setPasswordHash(resultSet.getString("password_hash"));
        authAccountDO.setDisplayName(resultSet.getString("display_name"));
        authAccountDO.setStatus(resultSet.getString("status"));
        return authAccountDO;
    }
}
