package com.youxuan.product.repository;

import com.youxuan.product.model.CategoryDO;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 类目表（categories）数据访问层。
 */
@Repository
public class CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public CategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询所有启用状态的类目，按层级和排序号排列。
     */
    public List<CategoryDO> findAllEnabled() {
        return jdbcTemplate.query(
                "SELECT * FROM categories WHERE status = 'ENABLED' AND deleted_at IS NULL ORDER BY level, sort_no",
                categoryRowMapper());
    }

    private RowMapper<CategoryDO> categoryRowMapper() {
        return (rs, rowNum) -> {
            CategoryDO c = new CategoryDO();
            c.setId(rs.getLong("id"));
            c.setParentId(rs.getLong("parent_id"));
            c.setCategoryName(rs.getString("category_name"));
            c.setLevel(rs.getInt("level"));
            c.setSortNo(rs.getInt("sort_no"));
            c.setStatus(rs.getString("status"));
            c.setRemark(rs.getString("remark"));
            c.setCreatedAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            return c;
        };
    }
}
