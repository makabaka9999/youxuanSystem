package com.youxuan.order.repository;

import com.youxuan.order.model.OrderStatusLogDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 订单状态变更日志数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的订单状态日志数据访问层，记录订单状态的每一次变更。
 * 每次订单状态变更均需调用 insert 记录日志，用于后续审计和问题排查。
 * </p>
 */
@Repository
public class OrderStatusLogRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, order_id, order_no, from_status, to_status, operator_type, operator_id, reason, request_id, remark, created_at";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增订单状态变更日志
     *
     * @param log 状态变更日志数据对象
     */
    public void insert(OrderStatusLogDO log) {
        String sql = "INSERT INTO order_status_logs (id, order_id, order_no, from_status, to_status, operator_type, operator_id, reason, request_id, remark, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        jdbcTemplate.update(sql,
                log.getId(), log.getOrderId(), log.getOrderNo(),
                log.getFromStatus(), log.getToStatus(),
                log.getOperatorType(), log.getOperatorId(),
                log.getReason(), log.getRequestId(), log.getRemark());
    }

    /**
     * 根据订单ID查询所有状态变更日志（按创建时间升序）
     *
     * @param orderId 订单ID
     * @return 状态变更日志列表
     */
    public List<OrderStatusLogDO> findByOrderId(Long orderId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM order_status_logs WHERE order_id = ? AND deleted_at IS NULL ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, new OrderStatusLogRowMapper(), orderId);
    }

    /**
     * 订单状态日志 RowMapper
     */
    private static class OrderStatusLogRowMapper implements RowMapper<OrderStatusLogDO> {
        @Override
        public OrderStatusLogDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderStatusLogDO log = new OrderStatusLogDO();
            log.setId(rs.getLong("id"));
            log.setOrderId(rs.getLong("order_id"));
            log.setOrderNo(rs.getString("order_no"));
            log.setFromStatus(rs.getString("from_status"));
            log.setToStatus(rs.getString("to_status"));
            log.setOperatorType(rs.getString("operator_type"));
            log.setOperatorId(rs.getLong("operator_id"));
            if (rs.wasNull()) {
                log.setOperatorId(null);
            }
            log.setReason(rs.getString("reason"));
            log.setRequestId(rs.getString("request_id"));
            log.setRemark(rs.getString("remark"));
            log.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            return log;
        }
    }
}
