package com.youxuan.merchant.repository;

import com.youxuan.merchant.model.SettlementAccountDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商家结算账户表（merchant_settlement_accounts）数据访问层。
 */
@Repository
public class SettlementAccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public SettlementAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商家 ID 查询所有结算账户。
     *
     * @param merchantId 商家 ID
     * @return 结算账户列表
     */
    public List<SettlementAccountDO> findByMerchantId(Long merchantId) {
        return jdbcTemplate.query(
                "SELECT * FROM merchant_settlement_accounts WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY id ASC",
                accountRowMapper(), merchantId);
    }

    /**
     * 根据主键 ID 查询结算账户。
     *
     * @param id 结算账户 ID
     * @return 结算账户对象，不存在返回 null
     */
    public SettlementAccountDO findById(Long id) {
        List<SettlementAccountDO> list = jdbcTemplate.query(
                "SELECT * FROM merchant_settlement_accounts WHERE id = ? AND deleted_at IS NULL",
                accountRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 新增结算账户。
     *
     * @param account 结算账户对象
     */
    public void insert(SettlementAccountDO account) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO merchant_settlement_accounts (id, merchant_id, account_type, account_name, " +
                "account_no_encrypted, account_no_masked, bank_name, audit_status, status, remark, " +
                "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                account.getId(), account.getMerchantId(), account.getAccountType(),
                account.getAccountName(), account.getAccountNoEncrypted(), account.getAccountNoMasked(),
                account.getBankName(), account.getAuditStatus(), account.getStatus(),
                account.getRemark(), now, now);
    }

    /**
     * 更新结算账户的审核状态。
     *
     * @param id          结算账户 ID
     * @param auditStatus 审核状态
     */
    public void updateAuditStatus(Long id, String auditStatus) {
        jdbcTemplate.update(
                "UPDATE merchant_settlement_accounts SET audit_status = ?, updated_at = ? WHERE id = ?",
                auditStatus, LocalDateTime.now(), id);
    }

    private RowMapper<SettlementAccountDO> accountRowMapper() {
        return (rs, rowNum) -> {
            SettlementAccountDO a = new SettlementAccountDO();
            a.setId(rs.getLong("id"));
            a.setMerchantId(rs.getLong("merchant_id"));
            a.setAccountType(rs.getString("account_type"));
            a.setAccountName(rs.getString("account_name"));
            a.setAccountNoEncrypted(rs.getString("account_no_encrypted"));
            a.setAccountNoMasked(rs.getString("account_no_masked"));
            a.setBankName(rs.getString("bank_name"));
            a.setAuditStatus(rs.getString("audit_status"));
            a.setStatus(rs.getString("status"));
            a.setRemark(rs.getString("remark"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            a.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            a.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            return a;
        };
    }
}
