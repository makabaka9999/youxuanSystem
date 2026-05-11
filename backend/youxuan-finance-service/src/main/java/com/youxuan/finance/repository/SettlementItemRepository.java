package com.youxuan.finance.repository;

import com.youxuan.finance.model.SettlementItemDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 结算单明细（settlement_order_items 表）数据访问层。
 * <p>
 * 支持按结算单 ID 查询明细列表和批量插入明细记录。
 * </p>
 */
@Repository
public class SettlementItemRepository {

    private final JdbcTemplate jdbcTemplate;

    public SettlementItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据结算单 ID 查询明细列表。
     *
     * @param settlementId 结算单 ID
     * @return 明细列表
     */
    public List<SettlementItemDO> findBySettlementId(Long settlementId) {
        return jdbcTemplate.query(
                "SELECT * FROM settlement_order_items WHERE settlement_id = ? ORDER BY id ASC",
                settlementItemRowMapper(), settlementId);
    }

    /**
     * 插入结算单明细。
     *
     * @param item 结算单明细数据对象
     */
    public void insert(SettlementItemDO item) {
        jdbcTemplate.update(
                "INSERT INTO settlement_order_items (id, settlement_id, merchant_id, order_id, order_item_id, " +
                "bill_id, payable_amount, commission_amount, refund_amount, status, remark, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                item.getId(), item.getSettlementId(), item.getMerchantId(),
                item.getOrderId(), item.getOrderItemId(), item.getBillId(),
                item.getPayableAmount(), item.getCommissionAmount(),
                item.getRefundAmount(), item.getStatus(), item.getRemark());
    }

    private RowMapper<SettlementItemDO> settlementItemRowMapper() {
        return (rs, rowNum) -> mapSettlementItem(rs);
    }

    private SettlementItemDO mapSettlementItem(ResultSet rs) throws SQLException {
        SettlementItemDO item = new SettlementItemDO();
        item.setId(rs.getLong("id"));
        item.setSettlementId(rs.getLong("settlement_id"));
        item.setMerchantId(rs.getLong("merchant_id"));
        item.setOrderId(rs.getLong("order_id"));
        item.setOrderItemId(rs.getLong("order_item_id"));
        item.setBillId(rs.getLong("bill_id"));
        item.setPayableAmount(rs.getBigDecimal("payable_amount"));
        item.setCommissionAmount(rs.getBigDecimal("commission_amount"));
        item.setRefundAmount(rs.getBigDecimal("refund_amount"));
        item.setStatus(rs.getString("status"));
        item.setRemark(rs.getString("remark"));
        item.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return item;
    }
}
