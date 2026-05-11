package com.youxuan.merchant.repository;

import com.youxuan.merchant.model.StoreDO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 店铺表（stores）数据访问层。
 */
@Repository
public class StoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public StoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据商家 ID 查询店铺信息。
     *
     * @param merchantId 商家 ID
     * @return 店铺对象，不存在返回 null
     */
    public StoreDO findByMerchantId(Long merchantId) {
        List<StoreDO> list = jdbcTemplate.query(
                "SELECT * FROM stores WHERE merchant_id = ? AND deleted_at IS NULL",
                storeRowMapper(), merchantId);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据商家 ID 和状态查询店铺。
     *
     * @param merchantId 商家 ID
     * @param status     店铺状态
     * @return 店铺对象，不存在返回 null
     */
    public StoreDO findByMerchantIdWithStatus(Long merchantId, String status) {
        List<StoreDO> list = jdbcTemplate.query(
                "SELECT * FROM stores WHERE merchant_id = ? AND status = ? AND deleted_at IS NULL",
                storeRowMapper(), merchantId, status);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 新增店铺。
     *
     * @param store 店铺对象
     */
    public void insert(StoreDO store) {
        LocalDateTime now = LocalDateTime.now();
        jdbcTemplate.update(
                "INSERT INTO stores (id, merchant_id, store_no, store_name, logo_url, contact_mobile, " +
                "category_id, status, remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                store.getId(), store.getMerchantId(), store.getStoreNo(), store.getStoreName(),
                store.getLogoUrl(), store.getContactMobile(), store.getCategoryId(),
                store.getStatus(), store.getRemark(), now, now);
    }

    /**
     * 更新店铺信息。
     *
     * @param store 店铺对象（仅更新非空字段）
     */
    public void update(StoreDO store) {
        jdbcTemplate.update(
                "UPDATE stores SET store_name = ?, logo_url = ?, contact_mobile = ?, category_id = ?, " +
                "status = ?, remark = ?, updated_at = ? WHERE id = ? AND deleted_at IS NULL",
                store.getStoreName(), store.getLogoUrl(), store.getContactMobile(),
                store.getCategoryId(), store.getStatus(), store.getRemark(),
                LocalDateTime.now(), store.getId());
    }

    private RowMapper<StoreDO> storeRowMapper() {
        return (rs, rowNum) -> {
            StoreDO s = new StoreDO();
            s.setId(rs.getLong("id"));
            s.setMerchantId(rs.getLong("merchant_id"));
            s.setStoreNo(rs.getString("store_no"));
            s.setStoreName(rs.getString("store_name"));
            s.setLogoUrl(rs.getString("logo_url"));
            s.setContactMobile(rs.getString("contact_mobile"));
            s.setCategoryId(rs.getObject("category_id") != null ? rs.getLong("category_id") : null);
            s.setStatus(rs.getString("status"));
            s.setRemark(rs.getString("remark"));
            Timestamp createdAt = rs.getTimestamp("created_at");
            s.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
            Timestamp updatedAt = rs.getTimestamp("updated_at");
            s.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
            return s;
        };
    }
}
