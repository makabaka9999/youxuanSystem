package com.youxuan.merchant.repository;

import com.youxuan.merchant.model.ProductDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 商品表（products）数据访问层（商家模块视角）。
 * 商家模块直接查询共享的商品数据库，仅操作归属于本商家的商品。
 */
@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商家 ID 分页查询商品列表（支持按名称搜索）。
     *
     * @param merchantId 商家 ID
     * @param keyword    搜索关键词（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 商品列表
     */
    public List<ProductDO> findByMerchantId(Long merchantId, String keyword, int pageNo, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE merchant_id = ? AND deleted_at IS NULL");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND product_name LIKE ?");
        }
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        int offset = (pageNo - 1) * pageSize;
        if (keyword != null && !keyword.isEmpty()) {
            return jdbcTemplate.query(sql.toString(), productRowMapper(), merchantId, "%" + keyword + "%", pageSize, offset);
        }
        return jdbcTemplate.query(sql.toString(), productRowMapper(), merchantId, pageSize, offset);
    }

    /**
     * 统计商家商品总数（支持按名称搜索）。
     *
     * @param merchantId 商家 ID
     * @param keyword    搜索关键词（可选）
     * @return 商品总数
     */
    public long countByMerchantId(Long merchantId, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products WHERE merchant_id = ? AND deleted_at IS NULL");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND product_name LIKE ?");
        }
        if (keyword != null && !keyword.isEmpty()) {
            Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, merchantId, "%" + keyword + "%");
            return count != null ? count : 0L;
        }
        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, merchantId);
        return count != null ? count : 0L;
    }

    /**
     * 根据 ID 查询商品。
     *
     * @param id 商品 ID
     * @return 商品对象，不存在返回 null
     */
    public ProductDO findById(Long id) {
        List<ProductDO> list = jdbcTemplate.query(
                "SELECT * FROM products WHERE id = ? AND deleted_at IS NULL",
                productRowMapper(), id);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 新增商品。
     *
     * @param product 商品对象
     */
    public void insert(ProductDO product) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO products (id, product_no, merchant_id, store_id, category_id, product_name, " +
                "main_image_url, detail_html, price, stock_total, audit_status, sale_status, reject_reason, " +
                "remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                product.getId(), product.getProductNo(), product.getMerchantId(), product.getStoreId(),
                product.getCategoryId(), product.getProductName(), product.getMainImageUrl(),
                product.getDetailHtml(), product.getPrice(), product.getStockTotal(),
                product.getAuditStatus(), product.getSaleStatus(), product.getRejectReason(),
                product.getRemark(), now, now);
    }

    /**
     * 更新商品销售状态。
     *
     * @param id         商品 ID
     * @param saleStatus 销售状态：ON_SALE / OFF_SALE
     */
    public void updateSaleStatus(Long id, String saleStatus) {
        jdbcTemplate.update(
                "UPDATE products SET sale_status = ?, updated_at = ? WHERE id = ? AND deleted_at IS NULL",
                saleStatus, LocalDateTime.now(), id);
    }

    /**
     * 统计商家指定销售状态的商品数。
     *
     * @param merchantId 商家 ID
     * @param saleStatus 销售状态
     * @return 商品数
     */
    public long countByMerchantIdAndSaleStatus(Long merchantId, String saleStatus) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE merchant_id = ? AND sale_status = ? AND deleted_at IS NULL",
                Long.class, merchantId, saleStatus);
        return count != null ? count : 0L;
    }

    /**
     * 统计商家指定审核状态的商品数。
     *
     * @param merchantId  商家 ID
     * @param auditStatus 审核状态
     * @return 商品数
     */
    public long countByMerchantIdAndAuditStatus(Long merchantId, String auditStatus) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE merchant_id = ? AND audit_status = ? AND deleted_at IS NULL",
                Long.class, merchantId, auditStatus);
        return count != null ? count : 0L;
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
            p.setPrice(rs.getBigDecimal("price"));
            p.setStockTotal(rs.getObject("stock_total") != null ? rs.getInt("stock_total") : null);
            p.setAuditStatus(rs.getString("audit_status"));
            p.setSaleStatus(rs.getString("sale_status"));
            p.setRejectReason(rs.getString("reject_reason"));
            p.setRemark(rs.getString("remark"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            p.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            p.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            return p;
        };
    }
}
