package com.youxuan.order.repository;

import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.model.CartDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 购物车数据仓库
 * <p>
 * 基于 JdbcTemplate 实现的购物车数据访问层，提供购物车项的增删改查操作。
 * 所有查询均过滤已软删除的记录（deleted_at IS NULL）。
 * </p>
 */
@Repository
public class CartRepository {

    /** 基础查询列 */
    private static final String BASE_COLUMNS = "id, user_id, store_id, product_id, sku_id, quantity, checked, status, remark, created_at, updated_at";

    /** 基础查询条件（排除软删除） */
    private static final String BASE_WHERE = "deleted_at IS NULL";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询用户的所有购物车项
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    public List<CartDO> findByUserId(Long userId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM carts WHERE user_id = ? AND " + BASE_WHERE;
        return jdbcTemplate.query(sql, new CartRowMapper(), userId);
    }

    /**
     * 根据ID查询购物车项
     *
     * @param id 购物车项ID
     * @return 购物车项，不存在时返回 null
     */
    public CartDO findById(Long id) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM carts WHERE id = ? AND " + BASE_WHERE;
        List<CartDO> results = jdbcTemplate.query(sql, new CartRowMapper(), id);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 根据用户ID和SKU ID查询购物车项
     *
     * @param userId 用户ID
     * @param skuId  SKU ID
     * @return 购物车项，不存在时返回 null
     */
    public CartDO findByUserAndSku(Long userId, Long skuId) {
        String sql = "SELECT " + BASE_COLUMNS + " FROM carts WHERE user_id = ? AND sku_id = ? AND " + BASE_WHERE;
        List<CartDO> results = jdbcTemplate.query(sql, new CartRowMapper(), userId, skuId);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * 新增购物车项
     *
     * @param cart 购物车数据对象
     */
    public void insert(CartDO cart) {
        String sql = "INSERT INTO carts (id, user_id, store_id, product_id, sku_id, quantity, checked, status, remark, created_at, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql,
                cart.getId(), cart.getUserId(), cart.getStoreId(), cart.getProductId(), cart.getSkuId(),
                cart.getQuantity(), cart.getChecked(), OrderStatusConstants.ENABLED, cart.getRemark());
    }

    /**
     * 更新购物车项数量
     *
     * @param id       购物车项ID
     * @param quantity 新的数量
     */
    public void updateQuantity(Long id, Integer quantity) {
        String sql = "UPDATE carts SET quantity = ?, updated_at = NOW() WHERE id = ? AND " + BASE_WHERE;
        jdbcTemplate.update(sql, quantity, id);
    }

    /**
     * 更新购物车项选中状态
     *
     * @param id      购物车项ID
     * @param checked 是否选中（1-选中，0-未选中）
     */
    public void updateChecked(Long id, Integer checked) {
        String sql = "UPDATE carts SET checked = ?, updated_at = NOW() WHERE id = ? AND " + BASE_WHERE;
        jdbcTemplate.update(sql, checked, id);
    }

    /**
     * 软删除购物车项
     *
     * @param id 购物车项ID
     */
    public void deleteById(Long id) {
        String sql = "UPDATE carts SET deleted_at = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    /**
     * 根据用户ID和SKU ID软删除购物车项
     *
     * @param userId 用户ID
     * @param skuId  SKU ID
     */
    public void deleteByUserAndSku(Long userId, Long skuId) {
        String sql = "UPDATE carts SET deleted_at = NOW() WHERE user_id = ? AND sku_id = ? AND " + BASE_WHERE;
        jdbcTemplate.update(sql, userId, skuId);
    }

    /**
     * 购物车 RowMapper，将结果集映射为 CartDO 对象
     */
    private static class CartRowMapper implements RowMapper<CartDO> {
        @Override
        public CartDO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CartDO cart = new CartDO();
            cart.setId(rs.getLong("id"));
            cart.setUserId(rs.getLong("user_id"));
            cart.setStoreId(rs.getLong("store_id"));
            cart.setProductId(rs.getLong("product_id"));
            cart.setSkuId(rs.getLong("sku_id"));
            cart.setQuantity(rs.getInt("quantity"));
            cart.setChecked(rs.getInt("checked"));
            cart.setStatus(rs.getString("status"));
            cart.setRemark(rs.getString("remark"));
            cart.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            cart.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return cart;
        }
    }
}
