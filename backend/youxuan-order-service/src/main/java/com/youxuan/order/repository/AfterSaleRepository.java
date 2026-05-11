package com.youxuan.order.repository;

import com.youxuan.order.model.AfterSaleDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 售后单数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的售后单数据访问层，提供售后单的查询、新增及状态更新操作。
 * 支持按用户、商家、订单及状态进行分页查询。
 * </p>
 */
@Repository
public class AfterSaleRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, after_sale_no, order_id, order_item_id, user_id, merchant_id, type, status, "
            + "apply_amount, approved_amount, reason, description, evidence_urls, merchant_reason, platform_reason, remark, created_at, updated_at";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据ID查询售后单
     *
     * @param id 售后单ID
     * @return 售后单对象，不存在时返回 null
     */
    public AfterSaleDO findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM after_sales WHERE id = ? AND deleted_at IS NULL";
        List<AfterSaleDO> results = jdbcTemplate.query(sql, new AfterSaleRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据订单ID查询所有售后单
     *
     * @param orderId 订单ID
     * @return 售后单列表
     */
    public List<AfterSaleDO> findByOrderId(Long orderId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM after_sales WHERE order_id = ? AND deleted_at IS NULL ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new AfterSaleRowMapper(), orderId);
    }

    /**
     * 分页查询用户的售后单
     *
     * @param userId   用户ID
     * @param pageNo   页码（从1开始）
     * @param pageSize 每页大小
     * @return 售后单列表
     */
    public List<AfterSaleDO> findByUserId(Long userId, int pageNo, int pageSize) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM after_sales WHERE user_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new AfterSaleRowMapper(), userId, pageSize, (pageNo - 1) * pageSize);
    }

    /**
     * 分页查询商家的售后单
     *
     * @param merchantId 商家ID
     * @param pageNo     页码（从1开始）
     * @param pageSize   每页大小
     * @return 售后单列表
     */
    public List<AfterSaleDO> findByMerchantId(Long merchantId, int pageNo, int pageSize) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM after_sales WHERE merchant_id = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new AfterSaleRowMapper(), merchantId, pageSize, (pageNo - 1) * pageSize);
    }

    /**
     * 按状态分页查询售后单（用于平台后台）
     *
     * @param status   售后状态
     * @param pageNo   页码（从1开始）
     * @param pageSize 每页大小
     * @return 售后单列表
     */
    public List<AfterSaleDO> findByStatus(String status, int pageNo, int pageSize) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM after_sales WHERE status = ? AND deleted_at IS NULL ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new AfterSaleRowMapper(), status, pageSize, (pageNo - 1) * pageSize);
    }

    /**
     * 统计商家的售后单数量，按状态筛选。
     */
    public int countByMerchantIdAndStatus(Long merchantId, String status) {
        String sql = "SELECT COUNT(*) FROM after_sales WHERE merchant_id = ? AND status = ? AND deleted_at IS NULL";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, merchantId, status);
        return result != null ? result : 0;
    }

    /**
     * 统计商家的售后单数量
     *
     * @param merchantId 商家ID
     * @return 售后单总数
     */
    public int countByMerchantId(Long merchantId) {
        String sql = "SELECT COUNT(*) FROM after_sales WHERE merchant_id = ? AND deleted_at IS NULL";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, merchantId);
        return result != null ? result : 0;
    }

    /**
     * 统计用户的售后单数量
     *
     * @param userId 用户ID
     * @return 售后单总数
     */
    public int countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM after_sales WHERE user_id = ? AND deleted_at IS NULL";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return result != null ? result : 0;
    }

    /**
     * 新增售后单
     *
     * @param afterSale 售后单数据对象
     */
    public void insert(AfterSaleDO afterSale) {
        String sql = "INSERT INTO after_sales (id, after_sale_no, order_id, order_item_id, user_id, merchant_id, type, status, "
                + "apply_amount, approved_amount, reason, description, evidence_urls, remark, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql,
                afterSale.getId(), afterSale.getAfterSaleNo(), afterSale.getOrderId(), afterSale.getOrderItemId(),
                afterSale.getUserId(), afterSale.getMerchantId(), afterSale.getType(), afterSale.getStatus(),
                afterSale.getApplyAmount(), afterSale.getApprovedAmount(), afterSale.getReason(),
                afterSale.getDescription(), afterSale.getEvidenceUrls(), afterSale.getRemark());
    }

    /**
     * 更新售后单状态
     * <p>
     * 根据 AfterSaleDO 对象中的字段更新售后单的状态、审核金额及处理意见。
     * </p>
     *
     * @param afterSale 包含最新状态的售后单对象
     */
    public void updateStatus(AfterSaleDO afterSale) {
        String sql = "UPDATE after_sales SET status = ?, approved_amount = ?, merchant_reason = ?, platform_reason = ?, "
                + "remark = ?, updated_at = NOW(), version = version + 1 WHERE id = ? AND deleted_at IS NULL";
        jdbcTemplate.update(sql,
                afterSale.getStatus(), afterSale.getApprovedAmount(),
                afterSale.getMerchantReason(), afterSale.getPlatformReason(),
                afterSale.getRemark(), afterSale.getId());
    }

    /**
     * 售后单 RowMapper
     */
    private static class AfterSaleRowMapper implements RowMapper<AfterSaleDO> {
        @Override
        public AfterSaleDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AfterSaleDO as = new AfterSaleDO();
            as.setId(rs.getLong("id"));
            as.setAfterSaleNo(rs.getString("after_sale_no"));
            as.setOrderId(rs.getLong("order_id"));
            as.setOrderItemId(rs.getLong("order_item_id"));
            if (rs.wasNull()) {
                as.setOrderItemId(null);
            }
            as.setUserId(rs.getLong("user_id"));
            as.setMerchantId(rs.getLong("merchant_id"));
            as.setType(rs.getString("type"));
            as.setStatus(rs.getString("status"));
            as.setApplyAmount(rs.getBigDecimal("apply_amount"));
            as.setApprovedAmount(rs.getBigDecimal("approved_amount"));
            as.setReason(rs.getString("reason"));
            as.setDescription(rs.getString("description"));
            as.setEvidenceUrls(rs.getString("evidence_urls"));
            as.setMerchantReason(rs.getString("merchant_reason"));
            as.setPlatformReason(rs.getString("platform_reason"));
            as.setRemark(rs.getString("remark"));
            as.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            as.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return as;
        }
    }
}
