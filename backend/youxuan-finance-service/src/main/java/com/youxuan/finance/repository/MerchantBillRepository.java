package com.youxuan.finance.repository;

import com.youxuan.finance.model.MerchantBillDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商户账单（merchant_bills 表）数据访问层。
 * <p>
 * 提供账单数据的查询、插入等持久化操作，
 * 支持按商户 ID、账单日期等维度检索账单信息。
 * </p>
 */
@Repository
public class MerchantBillRepository {

    private final JdbcTemplate jdbcTemplate;

    public MerchantBillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商户 ID 查询账单列表（按账单日期降序排列）。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 账单列表
     */
    public List<MerchantBillDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM merchant_bills WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY bill_date DESC LIMIT ? OFFSET ?",
                merchantBillRowMapper(), merchantId, pageSize, offset);
    }

    /**
     * 统计商户账单总数。
     *
     * @param merchantId 商户 ID
     * @return 账单总数
     */
    public int countByMerchantId(Long merchantId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM merchant_bills WHERE merchant_id = ? AND deleted_at IS NULL",
                Integer.class, merchantId);
        return count != null ? count : 0;
    }

    /**
     * 根据商户 ID 和账单日期查询账单。
     *
     * @param merchantId 商户 ID
     * @param billDate   账单日期
     * @return 账单列表
     */
    public List<MerchantBillDO> findByMerchantAndDate(Long merchantId, LocalDate billDate) {
        return jdbcTemplate.query(
                "SELECT * FROM merchant_bills WHERE merchant_id = ? AND bill_date = ? AND deleted_at IS NULL",
                merchantBillRowMapper(), merchantId, billDate);
    }

    /**
     * 根据账单日期查询所有商户的账单。
     *
     * @param billDate 账单日期
     * @return 账单列表
     */
    public List<MerchantBillDO> findByDate(LocalDate billDate) {
        return jdbcTemplate.query(
                "SELECT * FROM merchant_bills WHERE bill_date = ? AND deleted_at IS NULL",
                merchantBillRowMapper(), billDate);
    }

    /**
     * 插入商户账单记录。
     *
     * @param bill 账单数据对象
     */
    public void insert(MerchantBillDO bill) {
        jdbcTemplate.update(
                "INSERT INTO merchant_bills (id, merchant_id, bill_date, order_amount, freight_amount, " +
                "refund_amount, commission_amount, adjustment_amount, payable_amount, status, remark, " +
                "created_at, updated_at, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), 1)",
                bill.getId(), bill.getMerchantId(), bill.getBillDate(), bill.getOrderAmount(),
                bill.getFreightAmount(), bill.getRefundAmount(), bill.getCommissionAmount(),
                bill.getAdjustmentAmount(), bill.getPayableAmount(), bill.getStatus(), bill.getRemark());
    }

    /**
     * 更新账单状态。
     *
     * @param id     主键 ID
     * @param status 目标状态
     * @param version 乐观锁版本号
     * @return 影响的行数
     */
    public int updateStatus(Long id, String status, Integer version) {
        return jdbcTemplate.update(
                "UPDATE merchant_bills SET status = ?, updated_at = NOW(), version = version + 1 WHERE id = ? AND version = ?",
                status, id, version);
    }

    private RowMapper<MerchantBillDO> merchantBillRowMapper() {
        return (rs, rowNum) -> mapMerchantBill(rs);
    }

    private MerchantBillDO mapMerchantBill(ResultSet rs) throws SQLException {
        MerchantBillDO bill = new MerchantBillDO();
        bill.setId(rs.getLong("id"));
        bill.setMerchantId(rs.getLong("merchant_id"));
        bill.setBillDate(rs.getObject("bill_date", LocalDate.class));
        bill.setOrderAmount(rs.getBigDecimal("order_amount"));
        bill.setFreightAmount(rs.getBigDecimal("freight_amount"));
        bill.setRefundAmount(rs.getBigDecimal("refund_amount"));
        bill.setCommissionAmount(rs.getBigDecimal("commission_amount"));
        bill.setAdjustmentAmount(rs.getBigDecimal("adjustment_amount"));
        bill.setPayableAmount(rs.getBigDecimal("payable_amount"));
        bill.setStatus(rs.getString("status"));
        bill.setRemark(rs.getString("remark"));
        bill.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        bill.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return bill;
    }
}
