package com.youxuan.finance.repository;

import com.youxuan.finance.model.AccountFlowRecordDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 账户流水记录（account_flow_records 表）数据访问层。
 * <p>
 * 提供商户账户流水的查询和插入操作，
 * 用于资金变动的追溯和审计。
 * </p>
 */
@Repository
public class AccountFlowRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public AccountFlowRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商户 ID 分页查询流水记录。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 流水列表
     */
    public List<AccountFlowRecordDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM account_flow_records WHERE merchant_id = ? ORDER BY occurred_at DESC LIMIT ? OFFSET ?",
                accountFlowRowMapper(), merchantId, pageSize, offset);
    }

    /**
     * 统计商户流水总数。
     *
     * @param merchantId 商户 ID
     * @return 流水总数
     */
    public int countByMerchantId(Long merchantId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM account_flow_records WHERE merchant_id = ?",
                Integer.class, merchantId);
        return count != null ? count : 0;
    }

    /**
     * 插入账户流水记录。
     *
     * @param record 流水记录数据对象
     */
    public void insert(AccountFlowRecordDO record) {
        jdbcTemplate.update(
                "INSERT INTO account_flow_records (id, flow_no, merchant_id, biz_type, biz_no, direction, " +
                "amount, balance_before, balance_after, frozen_after, status, occurred_at, remark, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                record.getId(), record.getFlowNo(), record.getMerchantId(),
                record.getBizType(), record.getBizNo(), record.getDirection(),
                record.getAmount(), record.getBalanceBefore(), record.getBalanceAfter(),
                record.getFrozenAfter(), record.getStatus(), record.getOccurredAt(),
                record.getRemark());
    }

    private RowMapper<AccountFlowRecordDO> accountFlowRowMapper() {
        return (rs, rowNum) -> mapAccountFlow(rs);
    }

    private AccountFlowRecordDO mapAccountFlow(ResultSet rs) throws SQLException {
        AccountFlowRecordDO r = new AccountFlowRecordDO();
        r.setId(rs.getLong("id"));
        r.setFlowNo(rs.getString("flow_no"));
        r.setMerchantId(rs.getLong("merchant_id"));
        r.setBizType(rs.getString("biz_type"));
        r.setBizNo(rs.getString("biz_no"));
        r.setDirection(rs.getString("direction"));
        r.setAmount(rs.getBigDecimal("amount"));
        r.setBalanceBefore(rs.getBigDecimal("balance_before"));
        r.setBalanceAfter(rs.getBigDecimal("balance_after"));
        r.setFrozenAfter(rs.getBigDecimal("frozen_after"));
        r.setStatus(rs.getString("status"));
        r.setOccurredAt(rs.getTimestamp("occurred_at") != null
                ? rs.getTimestamp("occurred_at").toLocalDateTime() : null);
        r.setRemark(rs.getString("remark"));
        r.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return r;
    }
}
