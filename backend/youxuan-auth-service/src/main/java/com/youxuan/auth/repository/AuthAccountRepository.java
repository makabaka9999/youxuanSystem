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

/**
 * 账号仓储层（Repository）。
 * <p>
 * 使用 Spring JdbcTemplate 实现账号数据的持久化查询，
 * 支持按主体类型和账号检索账号信息，并自动填充角色和权限集合。
 * </p>
 */
@Repository
public class AuthAccountRepository {

    /** Spring JDBC 模板，用于执行数据库查询操作 */
    private final JdbcTemplate jdbcTemplate;

    /**
     * 构造账号仓储。
     *
     * @param jdbcTemplate Spring JDBC 模板
     */
    public AuthAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据主体类型和账号查询账号信息。
     * <p>
     * 平台管理员从 {@code platform_admins} 表查询，
     * 普通用户从 {@code users} 表查询。
     * 查询到后自动填充角色和权限信息。
     * </p>
     *
     * @param principalTypeEnum 主体类型枚举
     * @param account           登录账号（管理员用用户名，用户用手机号）
     * @return 包含账号信息的 Optional，未找到时返回 {@link Optional#empty()}
     */
    public Optional<AuthAccountDO> findByAccount(PrincipalTypeEnum principalTypeEnum, String account) {
        // 平台管理员走独立的查询逻辑
        if (principalTypeEnum == PrincipalTypeEnum.PLATFORM_ADMIN) {
            return findPlatformAdmin(account);
        }
        // 普通用户走用户表查询
        return findUser(account);
    }

    /**
     * 根据手机号查询普通用户账号信息。
     *
     * @param mobile 用户手机号
     * @return 包含账号信息的 Optional
     */
    private Optional<AuthAccountDO> findUser(String mobile) {
        List<AuthAccountDO> accountList = jdbcTemplate.query(
                "SELECT id, mobile, password_hash, nickname, status FROM users WHERE mobile = ? AND deleted_at IS NULL",
                (resultSet, rowNum) -> mapUser(resultSet),
                mobile);
        return accountList.stream().findFirst().map(this::fillAuthority);
    }

    /**
     * 根据用户名查询平台管理员账号信息。
     *
     * @param username 管理员用户名
     * @return 包含账号信息的 Optional
     */
    private Optional<AuthAccountDO> findPlatformAdmin(String username) {
        List<AuthAccountDO> accountList = jdbcTemplate.query(
                "SELECT id, username, password_hash, display_name, status FROM platform_admins WHERE username = ? AND deleted_at IS NULL",
                (resultSet, rowNum) -> mapPlatformAdmin(resultSet),
                username);
        return accountList.stream().findFirst().map(this::fillAuthority);
    }

    /**
     * 填充账号的角色和权限信息。
     * <p>
     * 通过关联表 {@code user_roles}、{@code roles}、
     * {@code role_permissions} 和 {@code permissions} 查询账号
     * 对应的角色编码集合和权限编码集合。
     * </p>
     *
     * @param authAccountDO 待填充的账号数据对象
     * @return 填充完成后的账号数据对象
     */
    private AuthAccountDO fillAuthority(AuthAccountDO authAccountDO) {
        // 查询角色编码集合
        Set<String> roleCodeSet = new HashSet<>(jdbcTemplate.queryForList(
                "SELECT r.role_code FROM user_roles ur JOIN roles r ON r.id = ur.role_id " +
                        "WHERE ur.principal_type = ? AND ur.principal_id = ? AND ur.deleted_at IS NULL AND r.deleted_at IS NULL",
                String.class,
                authAccountDO.getPrincipalType().name(),
                authAccountDO.getPrincipalId()));
        // 查询权限编码集合（通过角色-权限关联表间接查询）
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

    /**
     * 将 ResultSet 映射为普通用户的账号数据对象。
     *
     * @param resultSet 数据库查询结果集
     * @return 映射后的账号数据对象
     * @throws SQLException 数据库访问异常
     */
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

    /**
     * 将 ResultSet 映射为平台管理员的账号数据对象。
     *
     * @param resultSet 数据库查询结果集
     * @return 映射后的账号数据对象
     * @throws SQLException 数据库访问异常
     */
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
