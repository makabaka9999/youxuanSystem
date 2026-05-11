package com.youxuan.admin.repository;

import com.youxuan.admin.model.ExceptionOrderDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 异常单（exception_orders 表）数据访问层。
 * <p>
 * 提供异常单的查询、插入和状态更新操作，
 * 支持按 ID、异常类型、处理状态等维度检索。
 * </p>
 */
@Repository
public class ExceptionOrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public ExceptionOrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据 ID 查询异常单。
     *
     * @param id 异常单 ID
     * @return 异常单数据对象
     */
    public ExceptionOrderDO findById(Long id) {
        List<ExceptionOrderDO> list = jdbcTemplate.query(
                "SELECT * FROM exception_orders WHERE id = ? AND deleted_at IS NULL",
                exceptionRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据异常类型分页查询。
     *
     * @param exceptionType 异常类型
     * @param pageNo        页码
     * @param pageSize      每页条数
     * @return 异常单列表
     */
    public List<ExceptionOrderDO> findByType(String exceptionType, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM exception_orders WHERE exception_type = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                exceptionRowMapper(), exceptionType, pageSize, offset);
    }

    /**
     * 统计指定类型的异常单数量。
     *
     * @param exceptionType 异常类型
     * @return 数量
     */
    public int countByType(String exceptionType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM exception_orders WHERE exception_type = ? AND deleted_at IS NULL",
                Integer.class, exceptionType);
        return count != null ? count : 0;
    }

    /**
     * 分页查询所有异常单。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 异常单列表
     */
    public List<ExceptionOrderDO> findAll(int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM exception_orders WHERE deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                exceptionRowMapper(), pageSize, offset);
    }

    /**
     * 统计异常单总数。
     *
     * @return 总数
     */
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM exception_orders WHERE deleted_at IS NULL",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 按处理状态分页查询异常单。
     *
     * @param status   处理状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 异常单列表
     */
    public List<ExceptionOrderDO> findByStatus(String status, int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM exception_orders WHERE status = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?",
                exceptionRowMapper(), status, pageSize, offset);
    }

    /**
     * 统计指定状态的异常单数量。
     *
     * @param status 处理状态
     * @return 数量
     */
    public int countByStatus(String status) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM exception_orders WHERE status = ? AND deleted_at IS NULL",
                Integer.class, status);
        return count != null ? count : 0;
    }

    /**
     * 插入异常单记录。
     *
     * @param exception 异常单数据对象
     */
    public void insert(ExceptionOrderDO exception) {
        jdbcTemplate.update(
                "INSERT INTO exception_orders (id, exception_no, exception_type, biz_no, order_id, merchant_id, " +
                "severity, status, reason, suggestion, handle_result, handled_by, handled_at, remark, " +
                "created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                exception.getId(), exception.getExceptionNo(), exception.getExceptionType(),
                exception.getBizNo(), exception.getOrderId(), exception.getMerchantId(),
                exception.getSeverity(), exception.getStatus(), exception.getReason(),
                exception.getSuggestion(), exception.getHandleResult(), exception.getHandledBy(),
                exception.getHandledAt(), exception.getRemark());
    }

    /**
     * 更新异常单处理状态和处理结果。
     *
     * @param id          主键
     * @param status      目标状态
     * @param handleResult 处理结果
     * @param handledBy   处理人 ID
     * @return 影响行数
     */
    public int updateStatus(Long id, String status, String handleResult, Long handledBy) {
        return jdbcTemplate.update(
                "UPDATE exception_orders SET status = ?, handle_result = ?, handled_by = ?, " +
                "handled_at = NOW(), updated_at = NOW() WHERE id = ? AND deleted_at IS NULL",
                status, handleResult, handledBy, id);
    }

    private RowMapper<ExceptionOrderDO> exceptionRowMapper() {
        return (rs, rowNum) -> mapException(rs);
    }

    private ExceptionOrderDO mapException(ResultSet rs) throws SQLException {
        ExceptionOrderDO e = new ExceptionOrderDO();
        e.setId(rs.getLong("id"));
        e.setExceptionNo(rs.getString("exception_no"));
        e.setExceptionType(rs.getString("exception_type"));
        e.setBizNo(rs.getString("biz_no"));
        e.setOrderId(rs.getLong("order_id"));
        if (rs.wasNull()) e.setOrderId(null);
        e.setMerchantId(rs.getLong("merchant_id"));
        if (rs.wasNull()) e.setMerchantId(null);
        e.setSeverity(rs.getString("severity"));
        e.setStatus(rs.getString("status"));
        e.setReason(rs.getString("reason"));
        e.setSuggestion(rs.getString("suggestion"));
        e.setHandleResult(rs.getString("handle_result"));
        e.setHandledBy(rs.getLong("handled_by"));
        if (rs.wasNull()) e.setHandledBy(null);
        e.setHandledAt(rs.getTimestamp("handled_at") != null
                ? rs.getTimestamp("handled_at").toLocalDateTime() : null);
        e.setRemark(rs.getString("remark"));
        e.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        e.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return e;
    }
}
