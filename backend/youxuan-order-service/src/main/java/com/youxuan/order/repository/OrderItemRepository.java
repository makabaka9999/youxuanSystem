package com.youxuan.order.repository;

import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.model.OrderItemDO;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 订单明细数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的订单明细数据访问层，提供订单项的查询、新增及退款状态更新操作。
 * </p>
 */
@Repository
public class OrderItemRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, order_id, order_no, product_id, sku_id, product_snapshot, quantity, "
            + "sale_price, total_amount, refund_status, refunded_amount, refundable_amount, status, remark, created_at";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据订单ID查询所有明细
     *
     * @param orderId 订单ID
     * @return 订单明细列表
     */
    public List<OrderItemDO> findByOrderId(Long orderId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM order_items WHERE order_id = ? AND deleted_at IS NULL";
        return jdbcTemplate.query(sql, new OrderItemRowMapper(), orderId);
    }

    /**
     * 根据ID查询订单明细
     *
     * @param id 订单明细ID
     * @return 订单明细对象，不存在时返回 null
     */
    public OrderItemDO findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM order_items WHERE id = ? AND deleted_at IS NULL";
        List<OrderItemDO> results = jdbcTemplate.query(sql, new OrderItemRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 新增订单明细
     *
     * @param item 订单明细数据对象
     */
    public void insert(OrderItemDO item) {
        String sql = "INSERT INTO order_items (id, order_id, order_no, product_id, sku_id, product_snapshot, quantity, "
                + "sale_price, total_amount, refund_status, refunded_amount, refundable_amount, status, remark, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,
                item.getId(), item.getOrderId(), item.getOrderNo(), item.getProductId(), item.getSkuId(),
                item.getProductSnapshot(), item.getQuantity(), item.getSalePrice(), item.getTotalAmount(),
                OrderStatusConstants.REFUND_NONE, BigDecimal.ZERO, item.getTotalAmount(),
                OrderStatusConstants.ENABLED, item.getRemark());
    }

    /**
     * 更新订单明细的退款状态
     *
     * @param id             订单明细ID
     * @param refundStatus   退款状态
     * @param refundedAmount 已退款金额
     */
    public void updateRefundStatus(Long id, String refundStatus, BigDecimal refundedAmount) {
        String sql = "UPDATE order_items SET refund_status = ?, refunded_amount = ?, updated_at = NOW() WHERE id = ? AND deleted_at IS NULL";
        jdbcTemplate.update(sql, refundStatus, refundedAmount, id);
    }

    /**
     * 订单明细 RowMapper
     */
    private static class OrderItemRowMapper implements RowMapper<OrderItemDO> {
        @Override
        public OrderItemDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderItemDO item = new OrderItemDO();
            item.setId(rs.getLong("id"));
            item.setOrderId(rs.getLong("order_id"));
            item.setOrderNo(rs.getString("order_no"));
            item.setProductId(rs.getLong("product_id"));
            item.setSkuId(rs.getLong("sku_id"));
            item.setProductSnapshot(rs.getString("product_snapshot"));
            item.setQuantity(rs.getInt("quantity"));
            item.setSalePrice(rs.getBigDecimal("sale_price"));
            item.setTotalAmount(rs.getBigDecimal("total_amount"));
            item.setRefundStatus(rs.getString("refund_status"));
            item.setRefundedAmount(rs.getBigDecimal("refunded_amount"));
            item.setRefundableAmount(rs.getBigDecimal("refundable_amount"));
            item.setStatus(rs.getString("status"));
            item.setRemark(rs.getString("remark"));
            item.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            return item;
        }
    }
}
