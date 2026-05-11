package com.youxuan.merchant.repository;

import com.youxuan.merchant.model.MerchantApplicationDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商家入驻申请表（merchant_applications）数据访问层。
 */
@Repository
public class MerchantRepository {

    private final JdbcTemplate jdbcTemplate;

    public MerchantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据主键 ID 查询入驻申请。
     *
     * @param id 入驻申请 ID
     * @return 入驻申请对象，不存在返回 null
     */
    public MerchantApplicationDO findById(Long id) {
        List<MerchantApplicationDO> list = jdbcTemplate.query(
                "SELECT * FROM merchant_applications WHERE id = ? AND deleted_at IS NULL",
                merchantRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据所属用户 ID 查询入驻申请。
     *
     * @param ownerUserId 申请用户 ID
     * @return 入驻申请对象，不存在返回 null
     */
    public MerchantApplicationDO findByOwnerUserId(Long ownerUserId) {
        List<MerchantApplicationDO> list = jdbcTemplate.query(
                "SELECT * FROM merchant_applications WHERE owner_user_id = ? AND deleted_at IS NULL ORDER BY id DESC LIMIT 1",
                merchantRowMapper(), ownerUserId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 按审核状态分页查询入驻申请列表。
     *
     * @param auditStatus 审核状态
     * @param pageNo      页码
     * @param pageSize    每页条数
     * @return 入驻申请列表
     */
    public List<MerchantApplicationDO> findByAuditStatus(String auditStatus, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM merchant_applications WHERE audit_status = ? AND deleted_at IS NULL ORDER BY id DESC LIMIT ? OFFSET ?",
                merchantRowMapper(), auditStatus, pageSize, offset);
    }

    /**
     * 统计指定审核状态的申请总数。
     *
     * @param auditStatus 审核状态
     * @return 申请总数
     */
    public long countByAuditStatus(String auditStatus) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM merchant_applications WHERE audit_status = ? AND deleted_at IS NULL",
                Long.class, auditStatus);
        return count != null ? count : 0L;
    }

    /**
     * 新增入驻申请。
     *
     * @param merchant 入驻申请对象
     */
    public void insert(MerchantApplicationDO merchant) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO merchant_applications (id, merchant_no, owner_user_id, company_name, license_no, " +
                "contact_name, contact_mobile, audit_status, status, reject_reason, approved_at, remark, " +
                "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                merchant.getId(), merchant.getMerchantNo(), merchant.getOwnerUserId(),
                merchant.getCompanyName(), merchant.getLicenseNo(), merchant.getContactName(),
                merchant.getContactMobile(), merchant.getAuditStatus(), merchant.getStatus(),
                merchant.getRejectReason(), merchant.getApprovedAt(), merchant.getRemark(),
                now, now);
    }

    /**
     * 更新入驻申请的审核状态。
     *
     * @param id           入驻申请 ID
     * @param auditStatus  审核状态
     * @param rejectReason 驳回原因（审核通过时为 null）
     */
    public void updateAuditStatus(Long id, String auditStatus, String rejectReason) {
        LocalDateTime now = LocalDateTime.now();
        if ("APPROVED".equals(auditStatus)) {
            jdbcTemplate.update(
                    "UPDATE merchant_applications SET audit_status = ?, reject_reason = ?, approved_at = ?, updated_at = ? WHERE id = ?",
                    auditStatus, rejectReason, now, now, id);
        } else {
            jdbcTemplate.update(
                    "UPDATE merchant_applications SET audit_status = ?, reject_reason = ?, updated_at = ? WHERE id = ?",
                    auditStatus, rejectReason, now, id);
        }
    }

    /**
     * 更新入驻申请记录状态（启用/禁用）。
     *
     * @param id     入驻申请 ID
     * @param status 状态：ENABLED / DISABLED
     */
    public void updateStatus(Long id, String status) {
        jdbcTemplate.update(
                "UPDATE merchant_applications SET status = ?, updated_at = ? WHERE id = ?",
                status, LocalDateTime.now(), id);
    }

    private RowMapper<MerchantApplicationDO> merchantRowMapper() {
        return (rs, rowNum) -> {
            MerchantApplicationDO m = new MerchantApplicationDO();
            m.setId(rs.getLong("id"));
            m.setMerchantNo(rs.getString("merchant_no"));
            m.setOwnerUserId(rs.getLong("owner_user_id"));
            m.setCompanyName(rs.getString("company_name"));
            m.setLicenseNo(rs.getString("license_no"));
            m.setContactName(rs.getString("contact_name"));
            m.setContactMobile(rs.getString("contact_mobile"));
            m.setAuditStatus(rs.getString("audit_status"));
            m.setStatus(rs.getString("status"));
            m.setRejectReason(rs.getString("reject_reason"));
            Timestamp approvedAt = rs.getTimestamp("approved_at");
            m.setApprovedAt(approvedAt != null ? approvedAt.toLocalDateTime() : null);
            m.setRemark(rs.getString("remark"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            m.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            m.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            return m;
        };
    }
}
