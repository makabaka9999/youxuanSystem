package com.youxuan.product.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品类目数据对象，对应 categories 表。
 */
public class CategoryDO {

    /** 类目 ID */
    private Long id;

    /** 父类目 ID */
    private Long parentId;

    /** 类目名称 */
    private String categoryName;

    /** 层级 */
    private int level;

    /** 排序号 */
    private int sortNo;

    /** 状态：ENABLED / DISABLED */
    private String status;

    /** 备注 */
    private String remark;

    /** 子类目列表（仅树形结构中使用） */
    private List<CategoryDO> children = new ArrayList<>();

    /** 创建时间 */
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getSortNo() { return sortNo; }
    public void setSortNo(int sortNo) { this.sortNo = sortNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<CategoryDO> getChildren() { return children; }
    public void setChildren(List<CategoryDO> children) { this.children = children; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
