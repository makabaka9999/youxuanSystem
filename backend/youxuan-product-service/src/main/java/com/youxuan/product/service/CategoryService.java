package com.youxuan.product.service;

import com.youxuan.common.constant.CacheKeyConstants;
import com.youxuan.product.model.CategoryDO;
import com.youxuan.product.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 类目业务服务。
 */
@Service
public class CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public CategoryService(CategoryRepository categoryRepository, RedisTemplate<String, Object> redisTemplate) {
        this.categoryRepository = categoryRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取启用的类目树（按 parentId 组织为层级结构）。
     * <p>
     * 采用 Cache-Aside 模式：优先从 Redis 缓存读取，缓存未命中则从数据库重新构建并回写缓存（TTL = 600 秒）。
     * </p>
     */
    public List<CategoryDO> getCategoryTree() {
        // 1. 尝试从 Redis 缓存中获取类目树
        List<CategoryDO> cached = (List<CategoryDO>) redisTemplate.opsForValue().get(CacheKeyConstants.CATEGORY_TREE);
        if (cached != null) {
            LOGGER.debug("类目树缓存命中");
            return cached;
        }

        // 2. 缓存未命中，从数据库重新构建树
        List<CategoryDO> roots = buildCategoryTree();

        // 3. 写入缓存，TTL 600 秒（10 分钟）
        redisTemplate.opsForValue().set(CacheKeyConstants.CATEGORY_TREE, roots, 600, TimeUnit.SECONDS);
        LOGGER.debug("类目树已写入缓存");
        return roots;
    }

    /**
     * 从数据库查询所有启用的类目并构建树形结构。
     *
     * @return 类目树根节点列表
     */
    private List<CategoryDO> buildCategoryTree() {
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

    /**
     * 递归构建子节点列表。
     *
     * @param parentId  父类目 ID
     * @param parentMap 按 parentId 分组的类目映射
     * @return 子节点列表
     */
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

    /**
     * 清除类目树缓存。
     * <p>
     * 在类目信息发生变更（新增、更新、删除）时调用，保证下次读取时获取最新的类目树。
     * </p>
     */
    public void evictCategoryCache() {
        redisTemplate.delete(CacheKeyConstants.CATEGORY_TREE);
        LOGGER.debug("类目树缓存已清除");
    }
}
