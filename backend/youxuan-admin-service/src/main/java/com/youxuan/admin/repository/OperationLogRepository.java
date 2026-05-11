package com.youxuan.admin.repository;

import com.youxuan.admin.model.OperationLogDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 操作日志（operation_logs 表）数据访问层。
 * <p>
 * 提供操作日志的查询和插入功能，支持按模块、操作等
 * 维度检索审计日志。
 * </p>
 */
@Repository
public class OperationLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public OperationLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按模块编码和操作编码分页查询操作日志。
     *
     * @param moduleCode 模块编码（可选）
     * @param actionCode 操作编码（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 操作日志列表
     */
    public List<OperationLogDO> findByModuleAndAction(String moduleCode, String actionCode, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM operation_logs WHERE 1=1");
        if (moduleCode != null && !moduleCode.isEmpty()) {
            sql.append(" AND module_code = ?");
        }
        if (actionCode != null && !actionCode.isEmpty()) {
            sql.append(" AND action_code = ?");
        }
        sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");

        int offset = (pageNo - 1) * pageSize;
        // 构建参数
        Object[] params;
        if (moduleCode != null && !moduleCode.isEmpty() && actionCode != null && !actionCode.isEmpty()) {
            params = new Object[]{moduleCode, actionCode, pageSize, offset};
        } else if (moduleCode != null && !moduleCode.isEmpty()) {
            params = new Object[]{moduleCode, pageSize, offset};
        } else if (actionCode != null && !actionCode.isEmpty()) {
            params = new Object[]{actionCode, pageSize, offset};
        } else {
            params = new Object[]{pageSize, offset};
        }

        return jdbcTemplate.query(sql.toString(), operationLogRowMapper(), params);
    }

    /**
     * 统计操作日志总数（按筛选条件）。
     *
     * @param moduleCode 模块编码（可选）
     * @param actionCode 操作编码（可选）
     * @return 总数
     */
    public int countByModuleAndAction(String moduleCode, String actionCode) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM operation_logs WHERE 1=1");
        if (moduleCode != null && !moduleCode.isEmpty()) {
            sql.append(" AND module_code = ?");
        }
        if (actionCode != null && !actionCode.isEmpty()) {
            sql.append(" AND action_code = ?");
        }

        Object[] params;
        if (moduleCode != null && !moduleCode.isEmpty() && actionCode != null && !actionCode.isEmpty()) {
            params = new Object[]{moduleCode, actionCode};
        } else if (moduleCode != null && !moduleCode.isEmpty()) {
            params = new Object[]{moduleCode};
        } else if (actionCode != null && !actionCode.isEmpty()) {
            params = new Object[]{actionCode};
        } else {
            params = new Object[]{};
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params);
        return count != null ? count : 0;
    }

    /**
     * 分页查询所有操作日志。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 操作日志列表
     */
    public List<OperationLogDO> findAll(int pageNo, int pageSize) {
        int offset = (pageNo - 1) * pageSize;
        return jdbcTemplate.query(
                "SELECT * FROM operation_logs ORDER BY created_at DESC LIMIT ? OFFSET ?",
                operationLogRowMapper(), pageSize, offset);
    }

    /**
     * 统计操作日志总数。
     *
     * @return 总数
     */
    public int countAll() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM operation_logs",
                Integer.class);
        return count != null ? count : 0;
    }

    /**
     * 插入操作日志。
     *
     * @param log 操作日志数据对象
     */
    public void insert(OperationLogDO log) {
        jdbcTemplate.update(
                "INSERT INTO operation_logs (id, request_id, operator_type, operator_id, operator_name, " +
                "merchant_id, module_code, action_code, target_type, target_id, biz_type, biz_no, " +
                "before_snapshot, after_snapshot, request_ip, user_agent, result, fail_reason, status, " +
                "remark, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())",
                log.getId(), log.getRequestId(), log.getOperatorType(), log.getOperatorId(),
                log.getOperatorName(), log.getMerchantId(), log.getModuleCode(), log.getActionCode(),
                log.getTargetType(), log.getTargetId(), log.getBizType(), log.getBizNo(),
                log.getBeforeSnapshot(), log.getAfterSnapshot(), log.getRequestIp(), log.getUserAgent(),
                log.getResult(), log.getFailReason(), log.getStatus(), log.getRemark());
    }

    private RowMapper<OperationLogDO> operationLogRowMapper() {
        return (rs, rowNum) -> mapOperationLog(rs);
    }

    private OperationLogDO mapOperationLog(ResultSet rs) throws SQLException {
        OperationLogDO log = new OperationLogDO();
        log.setId(rs.getLong("id"));
        log.setRequestId(rs.getString("request_id"));
        log.setOperatorType(rs.getString("operator_type"));
        log.setOperatorId(rs.getLong("operator_id"));
        log.setOperatorName(rs.getString("operator_name"));
        log.setMerchantId(rs.getLong("merchant_id"));
        if (rs.wasNull()) log.setMerchantId(null);
        log.setModuleCode(rs.getString("module_code"));
        log.setActionCode(rs.getString("action_code"));
        log.setTargetType(rs.getString("target_type"));
        log.setTargetId(rs.getString("target_id"));
        log.setBizType(rs.getString("biz_type"));
        log.setBizNo(rs.getString("biz_no"));
        log.setBeforeSnapshot(rs.getString("before_snapshot"));
        log.setAfterSnapshot(rs.getString("after_snapshot"));
        log.setRequestIp(rs.getString("request_ip"));
        log.setUserAgent(rs.getString("user_agent"));
        log.setResult(rs.getString("result"));
        log.setFailReason(rs.getString("fail_reason"));
        log.setStatus(rs.getString("status"));
        log.setRemark(rs.getString("remark"));
        log.setCreatedAt(rs.getTimestamp("created_at") != null
                ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        return log;
    }
}
