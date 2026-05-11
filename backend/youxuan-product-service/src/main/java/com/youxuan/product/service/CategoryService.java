package com.youxuan.product.service;

import com.youxuan.product.model.CategoryDO;
import com.youxuan.product.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 类目业务服务。
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * 获取启用的类目树（按 parentId 组织为层级结构）。
     */
    public List<CategoryDO> getCategoryTree() {
        List<CategoryDO> all = categoryRepository.findAllEnabled();
        if (all.isEmpty()) {
            return all;
        }

        // 按 parentId 分组
        Map<Long, List<CategoryDO>> parentMap = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() != null ? c.getParentId() : 0L));

        // 构建树：找到根节点（parentId = 0 或 null），并设置其 children
        List<CategoryDO> roots = new ArrayList<>();
        for (CategoryDO category : all) {
            if (category.getParentId() == null || category.getParentId() == 0L) {
                category.setChildren(buildChildren(category.getId(), parentMap));
                roots.add(category);
            }
        }
        return roots;
    }

    private List<CategoryDO> buildChildren(Long parentId, Map<Long, List<CategoryDO>> parentMap) {
        List<CategoryDO> children = parentMap.get(parentId);
        if (children == null) {
            return new ArrayList<>();
        }
        for (CategoryDO child : children) {
            child.setChildren(buildChildren(child.getId(), parentMap));
        }
        return children;
    }
}
