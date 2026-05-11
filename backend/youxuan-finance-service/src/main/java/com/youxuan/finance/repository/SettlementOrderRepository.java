package com.youxuan.finance.repository;

import com.youxuan.finance.model.SettlementOrderDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 结算单（settlement_orders 表）数据访问层。
 * <p>
 * 提供结算单的增删改查操作，支持按商户 ID 查询、
 * 按主键查询、插入及状态更新等持久化操作。
 * </p>
 */
@Repository
public class SettlementOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public SettlementOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商户 ID 分页查询结算单列表（按创建时间降序）。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 结算单列表
     */
    public List<SettlementOrderDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM settlement_orders WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                settlementOrderRowMapper(), merchantId, pageSize, offset);
    }

    /**
     * 统计商户结算单总数。
     *
     * @param merchantId 商户 ID
     * @return 结算单总数
     */
    public int countByMerchantId(Long merchantId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM settlement_orders WHERE merchant_id = ? AND deleted_at IS NULL",
                Integer.class, merchantId);
        return count != null ? count : 0;
    }

    /**
     * 查询所有结算单（平台后台使用，分页）。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 结算单列表
     */
    public List<SettlementOrderDO> findAll(int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM settlement_orders WHERE deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                settlementOrderRowMapper(), pageSize, offset);
    }

    /**
     * 统计所有结算单总数。
     *
     * @return 结算单总数
     */
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM settlement_orders WHERE deleted_at IS NULL",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 查询指定状态的结算单列表。
     *
     * @param status   状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 结算单列表
     */
    public List<SettlementOrderDO> findByStatus(String status, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM settlement_orders WHERE status = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                settlementOrderRowMapper(), status, pageSize, offset);
    }

    /**
     * 统计指定状态的结算单总数。
     *
     * @param status 状态
     * @return 结算单总数
     */
    public int countByStatus(String status) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM settlement_orders WHERE status = ? AND deleted_at IS NULL",
                Integer.class, status);
        return count != null ? count : 0;
    }

    /**
     * 根据 ID 查询结算单。
     *
     * @param id 结算单 ID
     * @return 结算单数据对象，不存在时返回 null
     */
    public SettlementOrderDO findById(Long id) {
        List<SettlementOrderDO> list = jdbcTemplate.query(
                "SELECT * FROM settlement_orders WHERE id = ? AND deleted_at IS NULL",
                settlementOrderRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 插入结算单记录。
     *
     * @param settlement 结算单数据对象
     */
    public void insert(SettlementOrderDO settlement) {
        jdbcTemplate.update(
                "INSERT INTO settlement_orders (id, settlement_no, merchant_id, start_date, end_date, " +
                "order_amount, refund_amount, commission_amount, frozen_amount, payable_amount, status, " +
                "audit_user_id, audit_reason, approved_at, remark, created_at, updated_at, version) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)",
                settlement.getId(), settlement.getSettlementNo(), settlement.getMerchantId(),
                settlement.getStartDate(), settlement.getEndDate(),
                settlement.getOrderAmount(), settlement.getRefundAmount(),
                settlement.getCommissionAmount(), settlement.getFrozenAmount(),
                settlement.getPayableAmount(), settlement.getStatus(),
                settlement.getAuditUserId(), settlement.getAuditReason(),
                settlement.getApprovedAt(), settlement.getRemark());
    }

    /**
     * 更新结算单状态。
     *
     * @param id         主键
     * @param status     目标状态
     * @param auditUserId 审核人 ID
     * @param auditReason 审核意见
     * @param version    乐观锁版本号
     * @return 影响行数
     */
    public int updateStatus(Long id, String status, Long auditUserId, String auditReason, Integer version) {
        return jdbcTemplate.update(
                "UPDATE settlement_orders SET status = ?, audit_user_id = ?, audit_reason = ?, " +
                "approved_at = CASE WHEN ? IN ('APPROVED', 'PAID') THEN NOW() ELSE approved_at END, " +
                "updated_at = NOW(), version = version + 1 WHERE id = ? AND version = ?",
                status, auditUserId, auditReason, status, id, version);
    }

    /**
     * 更新状态（无审核信息，用于定时任务等场景）。
     *
     * @param id      主键
     * @param status  目标状态
     * @param version 乐观锁版本号
     * @return 影响行数
     */
    public int updateStatusDirect(Long id, String status, Integer version) {
        return jdbcTemplate.update(
                "UPDATE settlement_orders SET status = ?, updated_at = NOW(), version = version + 1 WHERE id = ? AND version = ?",
                status, id, version);
    }

    private RowMapper<SettlementOrderDO> settlementOrderRowMapper() {
        return (rs, rowNum) -> mapSettlementOrder(rs);
    }

    private SettlementOrderDO mapSettlementOrder(ResultSet rs) throws SQLException {
        SettlementOrderDO so = new SettlementOrderDO();
        so.setId(rs.getLong("id"));
        so.setSettlementNo(rs.getString("settlement_no"));
        so.setMerchantId(rs.getLong("merchant_id"));
        so.setStartDate(rs.getObject("start_date", java.time.LocalDate.class));
        so.setEndDate(rs.getObject("end_date", java.time.LocalDate.class));
        so.setOrderAmount(rs.getBigDecimal("order_amount"));
        so.setRefundAmount(rs.getBigDecimal("refund_amount"));
        so.setCommissionAmount(rs.getBigDecimal("commission_amount"));
        so.setFrozenAmount(rs.getBigDecimal("frozen_amount"));
        so.setPayableAmount(rs.getBigDecimal("payable_amount"));
        so.setStatus(rs.getString("status"));
        so.setAuditUserId(rs.getLong("audit_user_id"));
        if (rs.wasNull()) so.setAuditUserId(null);
        so.setAuditReason(rs.getString("audit_reason"));
        so.setApprovedAt(rs.getTimestamp("approved_at") != null
                ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
        so.setRemark(rs.getString("remark"));
        so.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        so.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        so.setVersion(rs.getInt("version"));
        return so;
    }
}
