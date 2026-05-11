package com.youxuan.finance.repository;

import com.youxuan.finance.model.ReconciliationRecordDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 对账记录（reconciliation_records 表）数据访问层。
 * <p>
 * 提供对账差异记录的查询、插入和状态更新操作，
 * 支持按支付渠道、对账日期及处理状态等维度检索。
 * </p>
 */
@Repository
public class ReconciliationRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReconciliationRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按渠道和对账日期查询对账记录。
     *
     * @param channel  支付渠道
     * @param billDate 对账日期
     * @return 对账记录列表
     */
    public List<ReconciliationRecordDO> findByChannelAndDate(String channel, LocalDate billDate) {
        return jdbcTemplate.query(
                "SELECT * FROM reconciliation_records WHERE channel = ? AND bill_date = ? ORDER BY id ASC",
                reconciliationRowMapper(), channel, billDate);
    }

    /**
     * 分页查询所有对账记录。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 对账记录列表
     */
    public List<ReconciliationRecordDO> findAll(int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM reconciliation_records ORDER BY created_at DESC LIMIT ? OFFSET ?",
                reconciliationRowMapper(), pageSize, offset);
    }

    /**
     * 统计对账记录总数。
     *
     * @return 总数
     */
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reconciliation_records",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 根据处理状态分页查询对账记录。
     *
     * @param handleStatus 处理状态
     * @param pageNo       页码
     * @param pageSize     每页条数
     * @return 对账记录列表
     */
    public List<ReconciliationRecordDO> findByHandleStatus(String handleStatus, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM reconciliation_records WHERE handle_status = ? ORDER BY created_at DESC LIMIT ? OFFSET ?",
                reconciliationRowMapper(), handleStatus, pageSize, offset);
    }

    /**
     * 统计指定处理状态的对账记录总数。
     *
     * @param handleStatus 处理状态
     * @return 总数
     */
    public int countByHandleStatus(String handleStatus) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reconciliation_records WHERE handle_status = ?",
                Integer.class, handleStatus);
        return count != null ? count : 0;
    }

    /**
     * 插入对账记录。
     *
     * @param record 对账记录数据对象
     */
    public void insert(ReconciliationRecordDO record) {
        jdbcTemplate.update(
                "INSERT INTO reconciliation_records (id, reconcile_no, channel, bill_date, biz_type, biz_no, " +
                "third_trade_no, platform_amount, channel_amount, diff_type, handle_status, handle_result, " +
                "remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                record.getId(), record.getReconcileNo(), record.getChannel(),
                record.getBillDate(), record.getBizType(), record.getBizNo(),
                record.getThirdTradeNo(), record.getPlatformAmount(), record.getChannelAmount(),
                record.getDiffType(), record.getHandleStatus(), record.getHandleResult(),
                record.getRemark());
    }

    /**
     * 更新对账记录的处理状态和结果。
     *
     * @param id           主键
     * @param handleStatus 处理状态
     * @param handleResult 处理结果
     */
    public void updateStatus(Long id, String handleStatus, String handleResult) {
        jdbcTemplate.update(
                "UPDATE reconciliation_records SET handle_status = ?, handle_result = ?, updated_at = NOW() WHERE id = ?",
                handleStatus, handleResult, id);
    }

    private RowMapper<ReconciliationRecordDO> reconciliationRowMapper() {
        return (rs, rowNum) -> mapReconciliation(rs);
    }

    private ReconciliationRecordDO mapReconciliation(ResultSet rs) throws SQLException {
        ReconciliationRecordDO r = new ReconciliationRecordDO();
        r.setId(rs.getLong("id"));
        r.setReconcileNo(rs.getString("reconcile_no"));
        r.setChannel(rs.getString("channel"));
        r.setBillDate(rs.getObject("bill_date", LocalDate.class));
        r.setBizType(rs.getString("biz_type"));
        r.setBizNo(rs.getString("biz_no"));
        r.setThirdTradeNo(rs.getString("third_trade_no"));
        r.setPlatformAmount(rs.getBigDecimal("platform_amount"));
        r.setChannelAmount(rs.getBigDecimal("channel_amount"));
        r.setDiffType(rs.getString("diff_type"));
        r.setHandleStatus(rs.getString("handle_status"));
        r.setHandleResult(rs.getString("handle_result"));
        r.setRemark(rs.getString("remark"));
        r.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        r.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return r;
    }
}
