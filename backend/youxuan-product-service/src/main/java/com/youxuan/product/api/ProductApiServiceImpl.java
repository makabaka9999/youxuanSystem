package com.youxuan.product.api;

import com.youxuan.api.product.ProductApiService;
import com.youxuan.product.model.ProductDO;
import com.youxuan.product.repository.ProductRepository;
import com.youxuan.product.service.ProductService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 商品服务 Dubbo RPC 实现。
 */
@DubboService
public class ProductApiServiceImpl implements ProductApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductApiServiceImpl.class);

    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductApiServiceImpl(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Override
    public String getProductName(Long productId) {
        ProductDO product = productService.getProductById(productId);
        return product != null ? product.getProductName() : null;
    }

    @Override
    public BigDecimal getSalePrice(Long skuId) {
        // TODO: SKU 模块尚未实现，待接入后补充查询逻辑
        LOGGER.warn("getSalePrice 被调用但 SKU 模块未实现，skuId={}", skuId);
        return null;
    }

    @Override
    public boolean isProductSaleable(Long productId, Long skuId, int quantity) {
        ProductDO product = productService.getProductById(productId);
        if (product == null) {
            LOGGER.warn("商品不存在，productId={}", productId);
            return false;
        }
        if (!"APPROVED".equals(product.getAuditStatus())) {
            LOGGER.warn("商品未通过审核，productId={}, auditStatus={}", productId, product.getAuditStatus());
            return false;
        }
        if (!"ON_SALE".equals(product.getSaleStatus())) {
            LOGGER.warn("商品未上架，productId={}, saleStatus={}", productId, product.getSaleStatus());
            return false;
        }
        // TODO: 校验 SKU 库存是否充足（待 SKU 模块实现）
        return true;
    }

    @Override
    public boolean lockStock(Long skuId, int quantity) {
        // TODO: 锁定 SKU 库存，待 SKU / 库存模块实现
        LOGGER.warn("lockStock 被调用但库存模块未实现，skuId={}, quantity={}", skuId, quantity);
        return true;
    }

    @Override
    public boolean releaseStock(Long skuId, int quantity) {
        // TODO: 释放 SKU 库存，待 SKU / 库存模块实现
        LOGGER.warn("releaseStock 被调用但库存模块未实现，skuId={}, quantity={}", skuId, quantity);
        return true;
    }

    @Override
    public int countMerchantProducts(Long merchantId) {
        return productRepository.countByMerchantId(merchantId);
    }
}
