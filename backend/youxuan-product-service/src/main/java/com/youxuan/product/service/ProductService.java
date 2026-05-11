package com.youxuan.product.service;

import com.youxuan.common.constant.CacheKeyConstants;
import com.youxuan.product.model.ProductDO;
import com.youxuan.product.repository.ProductRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 商品业务服务。
 */
@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    public ProductService(ProductRepository productRepository, RedisTemplate<String, Object> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 分页查询可售商品列表。
     *
     * @param keyword    搜索关键词
     * @param categoryId 类目筛选
     * @param pageNo     页码
     * @param pageSize   每页大小
     * @return 商品列表
     */
    public List<ProductDO> listOnSaleProducts(String keyword, Long categoryId, int pageNo, int pageSize) {
        return productRepository.findOnSaleProducts(keyword, categoryId, pageNo, pageSize);
    }

    /**
     * 统计可售商品总数。
     */
    public int countOnSaleProducts(String keyword, Long categoryId) {
        return productRepository.countOnSaleProducts(keyword, categoryId);
    }

    /**
     * 根据 ID 获取商品详情（优先从缓存读取，缓存未命中则查询数据库并回写缓存）。
     * <p>
     * 采用 Cache-Aside 模式：
     * 1. 先查 Redis 缓存；
     * 2. 缓存命中则直接返回；
     * 3. 缓存未命中则查数据库，并将结果写入缓存（TTL = 300 秒）。
     * </p>
     */
    public ProductDO getProductById(Long productId) {
        // 1. 尝试从 Redis 缓存中获取商品详情
        String cacheKey = CacheKeyConstants.PRODUCT_DETAIL_PREFIX + productId;
        ProductDO cached = (ProductDO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            LOGGER.debug("商品详情缓存命中. productId={}", productId);
            return cached;
        }

        // 2. 缓存未命中，从数据库查询
        ProductDO product = productRepository.findById(productId);
        if (product != null) {
            // 3. 写入缓存，TTL 300 秒（5 分钟）
            redisTemplate.opsForValue().set(cacheKey, product, 300, TimeUnit.SECONDS);
            LOGGER.debug("商品详情已写入缓存. productId={}", productId);
        }
        return product;
    }

    /**
     * 清除指定商品的缓存。
     * <p>
     * 在商品信息发生变更（如更新、删除）时调用，保证下次读取时获取最新数据。
     * </p>
     *
     * @param productId 商品 ID
     */
    private void evictProductCache(Long productId) {
        String cacheKey = CacheKeyConstants.PRODUCT_DETAIL_PREFIX + productId;
        redisTemplate.delete(cacheKey);
        LOGGER.debug("商品详情缓存已清除. productId={}", productId);
    }
}
