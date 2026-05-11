package com.youxuan.product.service;

import com.youxuan.product.model.ProductDO;
import com.youxuan.product.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 商品业务服务。
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
     * 根据 ID 获取商品详情。
     */
    public ProductDO getProductById(Long productId) {
        return productRepository.findById(productId);
    }
}
