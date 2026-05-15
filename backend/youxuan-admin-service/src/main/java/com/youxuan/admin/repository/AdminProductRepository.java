package com.youxuan.admin.repository;

import com.youxuan.admin.model.ProductAuditVO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商品表（products）数据访问层（平台后台视角）。
 * 用于审核队列查询和审核状态更新。
 */
@Repository
public class AdminProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public AdminProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 分页查询商品列表，含店铺名和类目名。
     *
     * @param keyword    搜索关键词（可选）
     * @param merchantId 商户 ID（可选）
     * @param status     审核状态筛选：PENDING / APPROVED / REJECTED，null 或空返回全部
     * @param pageNo     页码
     * @param pageSize   每页条数
     */
    public List<ProductAuditVO> findByAuditStatus(String keyword, Long merchantId, String status, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.product_name, p.main_image_url, p.price, p.stock_total, " +
                "p.audit_status, p.reject_reason, p.merchant_id, " +
                "s.store_name, c.category_name " +
                "FROM products p " +
                "LEFT JOIN stores s ON p.store_id = s.id " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND p.audit_status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND p.product_name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (merchantId != null) {
            sql.append(" AND p.merchant_id = ?");
            params.add(merchantId);
        }
        sql.append(" ORDER BY p.id DESC LIMIT ? OFFSET ?");
        int offset = (pageNo - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), productAuditRowMapper(), params.toArray());
    }

    /**
     * 统计商品总数。
     *
     * @param keyword    搜索关键词（可选）
     * @param merchantId 商户 ID（可选）
     * @param status     审核状态筛选，null 或空返回全部
     */
    public int countByAuditStatus(String keyword, Long merchantId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM products p WHERE p.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            sql.append(" AND p.audit_status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND p.product_name LIKE ?");
            params.add("%" + keyword + "%");
        }
        if (merchantId != null) {
            sql.append(" AND p.merchant_id = ?");
            params.add(merchantId);
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    /**
     * 更新商品审核状态。
     */
    public void updateAuditStatus(Long id, String auditStatus, String rejectReason) {
        jdbcTemplate.update(
                "UPDATE products SET audit_status = ?, reject_reason = ?, updated_at = NOW() WHERE id = ? AND deleted_at IS NULL",
                auditStatus, rejectReason, id);
    }

    private RowMapper<ProductAuditVO> productAuditRowMapper() {
        return (rs, rowNum) -> {
            ProductAuditVO vo = new ProductAuditVO();
            vo.setId(rs.getLong("id"));
            vo.setProductName(rs.getString("product_name"));
            vo.setMainImageUrl(rs.getString("main_image_url"));
            vo.setPrice(rs.getBigDecimal("price"));
            vo.setStockTotal(rs.getObject("stock_total") != null ? rs.getInt("stock_total") : null);
            vo.setAuditStatus(rs.getString("audit_status"));
            vo.setRejectReason(rs.getString("reject_reason"));
            vo.setMerchantId(rs.getLong("merchant_id"));
            vo.setStoreName(rs.getString("store_name"));
            vo.setCategoryName(rs.getString("category_name"));
            return vo;
        };
    }
}
