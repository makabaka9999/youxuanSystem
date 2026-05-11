package com.youxuan.order.repository;

import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.model.OrderDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 订单主表数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的订单数据访问层，支持订单的创建、状态更新、分页查询及超时订单扫描等操作。
 * 所有查询均过滤已软删除的记录（deleted_at IS NULL）。
 * </p>
 */
@Repository
public class OrderRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, order_no, user_id, merchant_id, store_id, order_status, pay_status, "
            + "total_amount, freight_amount, discount_amount, payable_amount, paid_amount, receiver_snapshot, "
            + "paid_at, shipped_at, completed_at, canceled_at, cancel_reason, remark, created_at, updated_at";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单对象，不存在时返回 null
     */
    public OrderDO findByOrderNo(String orderNo) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM orders WHERE order_no = ? AND deleted_at IS NULL";
        List<OrderDO> results = jdbcTemplate.query(sql, new OrderRowMapper(), orderNo);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单对象，不存在时返回 null
     */
    public OrderDO findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM orders WHERE id = ? AND deleted_at IS NULL";
        List<OrderDO> results = jdbcTemplate.query(sql, new OrderRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 分页查询用户的订单列表，可按订单状态筛选
     *
     * @param userId      用户ID
     * @param orderStatus 订单状态（为空则不限制）
     * @param pageNo      页码（从1开始）
     * @param pageSize    每页大小
     * @return 订单列表
     */
    public List<OrderDO> findByUserId(Long userId, String orderStatus, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT " + BASE_COLUMNS + " FROM orders WHERE user_id = ? AND deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (StringUtils.hasText(orderStatus)) {
            sql.append(" AND order_status = ?");
            params.add(orderStatus);
        }
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((pageNo - 1) * pageSize);
        return jdbcTemplate.query(sql.toString(), new OrderRowMapper(), params.toArray());
    }

    /**
     * 分页查询商家的订单列表
     *
     * @param merchantId 商家ID
     * @param pageNo     页码（从1开始）
     * @param pageSize   每页大小
     * @return 订单列表
     */
    public List<OrderDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM orders WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new OrderRowMapper(), merchantId, pageSize, (pageNo - 1) * pageSize);
    }

    /**
     * 统计用户订单数量
     *
     * @param userId      用户ID
     * @param orderStatus 订单状态（为空则不限制）
     * @return 订单总数
     */
    public int countByUserId(Long userId, String orderStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE user_id = ? AND deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        if (StringUtils.hasText(orderStatus)) {
            sql.append(" AND order_status = ?");
            params.add(orderStatus);
        }
        Integer result = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return result != null ? result : 0;
    }

    /**
     * 统计商家的订单数量，按状态筛选。
     */
    public int countByMerchantIdAndStatus(Long merchantId, String orderStatus) {
        String sql = "SELECT COUNT(*) FROM orders WHERE merchant_id = ? AND order_status = ? AND deleted_at IS NULL";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, merchantId, orderStatus);
        return result != null ? result : 0;
    }

    /**
     * 统计商家在指定日期范围内的订单总金额。
     */
    public java.math.BigDecimal sumAmountByMerchantIdAndDate(Long merchantId, String startDate, String endDate) {
        String sql = "SELECT COALESCE(SUM(payable_amount), 0) FROM orders WHERE merchant_id = ? AND deleted_at IS NULL "
                + "AND created_at >= ? AND created_at < DATE_ADD(?, INTERVAL 1 DAY)";
        return jdbcTemplate.queryForObject(sql, java.math.BigDecimal.class, merchantId, startDate, endDate);
    }

    /**
     * 统计商家的订单数量
     *
     * @param merchantId 商家ID
     * @return 订单总数
     */
    public int countByMerchantId(Long merchantId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE merchant_id = ? AND deleted_at IS NULL";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, merchantId);
        return result != null ? result : 0;
    }

    /**
     * 新增订单
     *
     * @param order 订单数据对象
     */
    public void insert(OrderDO order) {
        String sql = "INSERT INTO orders (id, order_no, user_id, merchant_id, store_id, order_status, pay_status, "
                + "total_amount, freight_amount, discount_amount, payable_amount, paid_amount, receiver_snapshot, "
                + "remark, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql,
                order.getId(), order.getOrderNo(), order.getUserId(), order.getMerchantId(), order.getStoreId(),
                order.getOrderStatus(), order.getPayStatus(),
                order.getTotalAmount(), order.getFreightAmount(), order.getDiscountAmount(),
                order.getPayableAmount(), order.getPaidAmount(), order.getReceiverSnapshot(),
                order.getRemark());
    }

    /**
     * 更新订单状态及相关时间戳
     * <p>
     * 根据 OrderDO 中的非空字段更新订单状态、支付状态、各种时间戳及版本号（乐观锁）。
     * </p>
     *
     * @param order 包含最新状态的订单对象
     * @return 更新的行数（可用于乐观锁校验）
     */
    public int updateStatus(OrderDO order) {
        String sql = "UPDATE orders SET order_status = ?, pay_status = ?, paid_at = ?, shipped_at = ?, completed_at = ?, "
                + "canceled_at = ?, cancel_reason = ?, remark = ?, updated_at = NOW(), version = version + 1 "
                + "WHERE id = ? AND deleted_at IS NULL";
        return jdbcTemplate.update(sql,
                order.getOrderStatus(), order.getPayStatus(),
                order.getPaidAt(), order.getShippedAt(), order.getCompletedAt(), order.getCanceledAt(),
                order.getCancelReason(), order.getRemark(),
                order.getId());
    }

    /**
     * 查询超时未支付的订单
     * <p>
     * 查找创建时间超过指定分钟数、状态仍为 CREATED + UNPAID 的订单，用于自动取消关单。
     * </p>
     *
     * @param minutes 超时分钟数
     * @return 超时订单列表
     */
    public List<OrderDO> findTimeoutOrders(int minutes) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM orders WHERE order_status = ? AND pay_status = ? AND deleted_at IS NULL "
                + "AND created_at <= DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        return jdbcTemplate.query(sql, new OrderRowMapper(),
                OrderStatusConstants.ORDER_CREATED, OrderStatusConstants.PAY_UNPAID, minutes);
    }

    /**
     * 订单 RowMapper，将结果集映射为 OrderDO 对象
     */
    private static class OrderRowMapper implements RowMapper<OrderDO> {
        @Override
        public OrderDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderDO order = new OrderDO();
            order.setId(rs.getLong("id"));
            order.setOrderNo(rs.getString("order_no"));
            order.setUserId(rs.getLong("user_id"));
            order.setMerchantId(rs.getLong("merchant_id"));
            order.setStoreId(rs.getLong("store_id"));
            order.setOrderStatus(rs.getString("order_status"));
            order.setPayStatus(rs.getString("pay_status"));
            order.setTotalAmount(rs.getBigDecimal("total_amount"));
            order.setFreightAmount(rs.getBigDecimal("freight_amount"));
            order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
            order.setPayableAmount(rs.getBigDecimal("payable_amount"));
            order.setPaidAmount(rs.getBigDecimal("paid_amount"));
            order.setReceiverSnapshot(rs.getString("receiver_snapshot"));
            order.setPaidAt(rs.getTimestamp("paid_at") != null ? rs.getTimestamp("paid_at").toLocalDateTime() : null);
            order.setShippedAt(rs.getTimestamp("shipped_at") != null ? rs.getTimestamp("shipped_at").toLocalDateTime() : null);
            order.setCompletedAt(rs.getTimestamp("completed_at") != null ? rs.getTimestamp("completed_at").toLocalDateTime() : null);
            order.setCanceledAt(rs.getTimestamp("canceled_at") != null ? rs.getTimestamp("canceled_at").toLocalDateTime() : null);
            order.setCancelReason(rs.getString("cancel_reason"));
            order.setRemark(rs.getString("remark"));
            order.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            order.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return order;
        }
    }
}
