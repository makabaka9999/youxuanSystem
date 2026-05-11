package com.youxuan.finance.repository;

import com.youxuan.finance.model.WithdrawOrderDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 提现单（withdraw_orders 表）数据访问层。
 * <p>
 * 提供提现单的查询、插入及状态更新操作，
 * 支持按商户 ID、按 ID 及按状态等维度检索。
 * </p>
 */
@Repository
public class WithdrawOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public WithdrawOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商户 ID 分页查询提现单列表。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 提现单列表
     */
    public List<WithdrawOrderDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM withdraw_orders WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                withdrawOrderRowMapper(), merchantId, pageSize, offset);
    }

    /**
     * 统计商户提现单总数。
     *
     * @param merchantId 商户 ID
     * @return 提现单总数
     */
    public int countByMerchantId(Long merchantId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM withdraw_orders WHERE merchant_id = ? AND deleted_at IS NULL",
                Integer.class, merchantId);
        return count != null ? count : 0;
    }

    /**
     * 根据 ID 查询提现单。
     *
     * @param id 提现单 ID
     * @return 提现单数据对象
     */
    public WithdrawOrderDO findById(Long id) {
        List<WithdrawOrderDO> list = jdbcTemplate.query(
                "SELECT * FROM withdraw_orders WHERE id = ? AND deleted_at IS NULL",
                withdrawOrderRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询所有提现单（平台后台使用，分页）。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 提现单列表
     */
    public List<WithdrawOrderDO> findAll(int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM withdraw_orders WHERE deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                withdrawOrderRowMapper(), pageSize, offset);
    }

    /**
     * 统计所有提现单总数。
     *
     * @return 提现单总数
     */
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM withdraw_orders WHERE deleted_at IS NULL",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 根据状态查询提现单列表。
     *
     * @param status   状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 提现单列表
     */
    public List<WithdrawOrderDO> findByStatus(String status, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM withdraw_orders WHERE status = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                withdrawOrderRowMapper(), status, pageSize, offset);
    }

    /**
     * 统计指定状态的提现单总数。
     *
     * @param status 状态
     * @return 提现单总数
     */
    public int countByStatus(String status) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM withdraw_orders WHERE status = ? AND deleted_at IS NULL",
                Integer.class, status);
        return count != null ? count : 0;
    }

    /**
     * 插入提现单记录。
     *
     * @param withdraw 提现单数据对象
     */
    public void insert(WithdrawOrderDO withdraw) {
        jdbcTemplate.update(
                "INSERT INTO withdraw_orders (id, withdraw_no, merchant_id, account_id, settlement_id, " +
                "amount, status, audit_user_id, audit_reason, fail_reason, paid_at, idempotent_key, remark, " +
                "created_at, updated_at, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)",
                withdraw.getId(), withdraw.getWithdrawNo(), withdraw.getMerchantId(),
                withdraw.getAccountId(), withdraw.getSettlementId(), withdraw.getAmount(),
                withdraw.getStatus(), withdraw.getAuditUserId(), withdraw.getAuditReason(),
                withdraw.getFailReason(), withdraw.getPaidAt(), withdraw.getIdempotentKey(),
                withdraw.getRemark());
    }

    /**
     * 更新提现单状态（含审核信息）。
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
                "UPDATE withdraw_orders SET status = ?, audit_user_id = ?, audit_reason = ?, " +
                "updated_at = NOW(), version = version + 1 WHERE id = ? AND version = ?",
                status, auditUserId, auditReason, id, version);
    }

    /**
     * 更新提现单状态（含支付结果）。
     *
     * @param id        主键
     * @param status    目标状态
     * @param failReason 失败原因（可选）
     * @param paidAt    付款时间（可选）
     * @param version   乐观锁版本号
     * @return 影响行数
     */
    public int updatePayStatus(Long id, String status, String failReason, java.time.LocalDateTime paidAt, Integer version) {
        return jdbcTemplate.update(
                "UPDATE withdraw_orders SET status = ?, fail_reason = ?, paid_at = ?, " +
                "updated_at = NOW(), version = version + 1 WHERE id = ? AND version = ?",
                status, failReason, paidAt, id, version);
    }

    private RowMapper<WithdrawOrderDO> withdrawOrderRowMapper() {
        return (rs, rowNum) -> mapWithdrawOrder(rs);
    }

    private WithdrawOrderDO mapWithdrawOrder(ResultSet rs) throws SQLException {
        WithdrawOrderDO wo = new WithdrawOrderDO();
        wo.setId(rs.getLong("id"));
        wo.setWithdrawNo(rs.getString("withdraw_no"));
        wo.setMerchantId(rs.getLong("merchant_id"));
        wo.setAccountId(rs.getLong("account_id"));
        wo.setSettlementId(rs.getLong("settlement_id"));
        wo.setAmount(rs.getBigDecimal("amount"));
        wo.setStatus(rs.getString("status"));
        wo.setAuditUserId(rs.getLong("audit_user_id"));
        if (rs.wasNull()) wo.setAuditUserId(null);
        wo.setAuditReason(rs.getString("audit_reason"));
        wo.setFailReason(rs.getString("fail_reason"));
        wo.setPaidAt(rs.getTimestamp("paid_at") != null
                ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
        wo.setIdempotentKey(rs.getString("idempotent_key"));
        wo.setRemark(rs.getString("remark"));
        wo.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        wo.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        wo.setVersion(rs.getInt("version"));
        return wo;
    }
}
