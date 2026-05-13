package com.youxuan.merchant.repository;

import com.youxuan.merchant.model.MerchantStaffDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商家员工表（merchant_staffs）数据访问层。
 */
@Repository
public class MerchantStaffRepository {

    private final JdbcTemplate jdbcTemplate;

    public MerchantStaffRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商家 ID 查询所有员工。
     *
     * @param merchantId 商家 ID
     * @return 员工列表
     */
    public List<MerchantStaffDO> findByMerchantId(Long merchantId) {
        return jdbcTemplate.query(
                "SELECT * FROM merchant_staffs WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY id ASC",
                staffRowMapper(), merchantId);
    }

    /**
     * 根据用户 ID 查询员工信息。
     *
     * @param userId 用户 ID
     * @return 员工对象，不存在返回 null
     */
    public MerchantStaffDO findByUserId(Long userId) {
        List<MerchantStaffDO> list = jdbcTemplate.query(
                "SELECT * FROM merchant_staffs WHERE user_id = ? AND deleted_at IS NULL",
                staffRowMapper(), userId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据手机号查询用户 ID。
     *
     * @param mobile 手机号
     * @return 用户 ID，不存在返回 null
     */
    public Long findUserIdByMobile(String mobile) {
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM users WHERE mobile = ? AND deleted_at IS NULL",
                Long.class, mobile);
        return ids.isEmpty() ? null : ids.get(0);
    }

    /**
     * 根据商家 ID 和用户 ID 查询员工。
     *
     * @param merchantId 商家 ID
     * @param userId     用户 ID
     * @return 员工对象，不存在返回 null
     */
    public MerchantStaffDO findByMerchantAndUser(Long merchantId, Long userId) {
        List<MerchantStaffDO> list = jdbcTemplate.query(
                "SELECT * FROM merchant_staffs WHERE merchant_id = ? AND user_id = ? AND deleted_at IS NULL",
                staffRowMapper(), merchantId, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 新增员工。
     *
     * @param staff 员工对象
     */
    public void insert(MerchantStaffDO staff) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO merchant_staffs (id, merchant_id, user_id, staff_name, role_type, " +
                "menu_permissions, status, remark, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                staff.getId(), staff.getMerchantId(), staff.getUserId(), staff.getStaffName(),
                staff.getRoleType(), staff.getMenuPermissions(), staff.getStatus(),
                staff.getRemark(), now, now);
    }

    /**
     * 更新员工状态（启用/禁用）。
     *
     * @param id     员工 ID
     * @param status 状态：ENABLED / DISABLED
     */
    public void updateStatus(Long id, String status) {
        jdbcTemplate.update(
                "UPDATE merchant_staffs SET status = ?, updated_at = ? WHERE id = ? AND deleted_at IS NULL",
                status, LocalDateTime.now(), id);
    }

    private RowMapper<MerchantStaffDO> staffRowMapper() {
        return (rs, rowNum) -> {
            MerchantStaffDO s = new MerchantStaffDO();
            s.setId(rs.getLong("id"));
            s.setMerchantId(rs.getLong("merchant_id"));
            s.setUserId(rs.getLong("user_id"));
            s.setStaffName(rs.getString("staff_name"));
            s.setRoleType(rs.getString("role_type"));
            s.setMenuPermissions(rs.getString("menu_permissions"));
            s.setStatus(rs.getString("status"));
            Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
            s.setLastLoginAt(lastLoginAt != null ? lastLoginAt.toLocalDateTime() : null);
            s.setRemark(rs.getString("remark"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            s.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            s.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            return s;
        };
    }
}
