package com.youxuan.product.repository;

import com.youxuan.product.model.ProductDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商品表（products）数据访问层。
 */
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询已上架且审核通过的商品列表。
     *
     * @param keyword    关键词（匹配商品名称）
     * @param categoryId 类目 ID（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 商品列表
     */
    public List<ProductDO> findOnSaleProducts(String keyword, Long categoryId, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder(
                "SELECT p.* FROM products p " +
                "WHERE p.audit_status = 'APPROVED' AND p.sale_status = 'ON_SALE' AND p.deleted_at IS NULL");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND MATCH(p.product_name) AGAINST(? IN BOOLEAN MODE)");
        }
        if (categoryId != null) {
            sql.append(" AND p.category_id = ?");
        }
        sql.append(" ORDER BY p.created_at DESC LIMIT ? OFFSET ?");

        Object[] params;
        int offset = (pageNo - 1) * pageSize;
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            params = new Object[]{keyword + "*", categoryId, pageSize, offset};
        } else if (keyword != null && !keyword.isEmpty()) {
            params = new Object[]{keyword + "*", pageSize, offset};
        } else if (categoryId != null) {
            params = new Object[]{categoryId, pageSize, offset};
        } else {
            params = new Object[]{pageSize, offset};
        }

        return jdbcTemplate.query(sql.toString(), productRowMapper(), params);
    }

    /**
     * 按 ID 查询商品。
     */
    public ProductDO findById(Long productId) {
        List<ProductDO> list = jdbcTemplate.query(
                "SELECT * FROM products WHERE id = ? AND deleted_at IS NULL",
                productRowMapper(), productId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 统计可售商品总数。
     */
    public int countOnSaleProducts(String keyword, Long categoryId) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM products p " +
                "WHERE p.audit_status = 'APPROVED' AND p.sale_status = 'ON_SALE' AND p.deleted_at IS NULL");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND MATCH(p.product_name) AGAINST(? IN BOOLEAN MODE)");
        }
        if (categoryId != null) {
            sql.append(" AND p.category_id = ?");
        }

        Object[] params;
        if (keyword != null && !keyword.isEmpty() && categoryId != null) {
            params = new Object[]{keyword + "*", categoryId};
        } else if (keyword != null && !keyword.isEmpty()) {
            params = new Object[]{keyword + "*"};
        } else if (categoryId != null) {
            params = new Object[]{categoryId};
        } else {
            params = new Object[]{};
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params);
        return count != null ? count : 0;
    }

    private RowMapper<ProductDO> productRowMapper() {
        return (rs, rowNum) -> {
            ProductDO p = new ProductDO();
            p.setId(rs.getLong("id"));
            p.setProductNo(rs.getString("product_no"));
            p.setMerchantId(rs.getLong("merchant_id"));
            p.setStoreId(rs.getLong("store_id"));
            p.setCategoryId(rs.getLong("category_id"));
            p.setProductName(rs.getString("product_name"));
            p.setMainImageUrl(rs.getString("main_image_url"));
            p.setDetailHtml(rs.getString("detail_html"));
            p.setAuditStatus(rs.getString("audit_status"));
            p.setSaleStatus(rs.getString("sale_status"));
            p.setRejectReason(rs.getString("reject_reason"));
            p.setRemark(rs.getString("remark"));
            p.setCreatedAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            p.setUpdatedAt(rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return p;
        };
    }
}
