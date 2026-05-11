package com.youxuan.order.repository;

import com.youxuan.order.model.PaymentOrderDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 支付单数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的支付单数据访问层，提供支付单的查询、新增及状态更新操作。
 * 支持按支付单号、订单号、幂等Key查询。
 * </p>
 */
@Repository
public class PaymentOrderRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, payment_no, order_id, order_no, user_id, channel, pay_amount, pay_status, "
            + "third_trade_no, idempotent_key, paid_at, callback_payload, remark, created_at, updated_at";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据支付单号查询支付单
     *
     * @param paymentNo 支付单号
     * @return 支付单对象，不存在时返回 null
     */
    public PaymentOrderDO findByPaymentNo(String paymentNo) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM payment_orders WHERE payment_no = ? AND deleted_at IS NULL";
        List<PaymentOrderDO> results = jdbcTemplate.query(sql, new PaymentOrderRowMapper(), paymentNo);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据订单ID查询支付单
     *
     * @param orderId 订单ID
     * @return 支付单对象，不存在时返回 null
     */
    public PaymentOrderDO findByOrderId(Long orderId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM payment_orders WHERE order_id = ? AND deleted_at IS NULL";
        List<PaymentOrderDO> results = jdbcTemplate.query(sql, new PaymentOrderRowMapper(), orderId);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据幂等Key查询支付单
     *
     * @param key 幂等Key
     * @return 支付单对象，不存在时返回 null
     */
    public PaymentOrderDO findByIdempotentKey(String key) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM payment_orders WHERE idempotent_key = ? AND deleted_at IS NULL";
        List<PaymentOrderDO> results = jdbcTemplate.query(sql, new PaymentOrderRowMapper(), key);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 新增支付单
     *
     * @param payment 支付单数据对象
     */
    public void insert(PaymentOrderDO payment) {
        String sql = "INSERT INTO payment_orders (id, payment_no, order_id, order_no, user_id, channel, pay_amount, pay_status, "
                + "idempotent_key, remark, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql,
                payment.getId(), payment.getPaymentNo(), payment.getOrderId(), payment.getOrderNo(), payment.getUserId(),
                payment.getChannel(), payment.getPayAmount(), payment.getPayStatus(),
                payment.getIdempotentKey(), payment.getRemark());
    }

    /**
     * 更新支付单状态
     *
     * @param payment 包含最新状态的支付单对象
     */
    public void updateStatus(PaymentOrderDO payment) {
        String sql = "UPDATE payment_orders SET pay_status = ?, third_trade_no = ?, paid_at = ?, callback_payload = ?, "
                + "updated_at = NOW(), version = version + 1 WHERE id = ? AND deleted_at IS NULL";
        jdbcTemplate.update(sql,
                payment.getPayStatus(), payment.getThirdTradeNo(), payment.getPaidAt(),
                payment.getCallbackPayload(), payment.getId());
    }

    /**
     * 支付单 RowMapper
     */
    private static class PaymentOrderRowMapper implements RowMapper<PaymentOrderDO> {
        @Override
        public PaymentOrderDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaymentOrderDO payment = new PaymentOrderDO();
            payment.setId(rs.getLong("id"));
            payment.setPaymentNo(rs.getString("payment_no"));
            payment.setOrderId(rs.getLong("order_id"));
            payment.setOrderNo(rs.getString("order_no"));
            payment.setUserId(rs.getLong("user_id"));
            payment.setChannel(rs.getString("channel"));
            payment.setPayAmount(rs.getBigDecimal("pay_amount"));
            payment.setPayStatus(rs.getString("pay_status"));
            payment.setThirdTradeNo(rs.getString("third_trade_no"));
            payment.setIdempotentKey(rs.getString("idempotent_key"));
            payment.setPaidAt(rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
            payment.setCallbackPayload(rs.getString("callback_payload"));
            payment.setRemark(rs.getString("remark"));
            payment.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            payment.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return payment;
        }
    }
}
