package com.youxuan.merchant.service;

import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.merchant.dto.CreateProductRequest;
import com.youxuan.merchant.model.ProductDO;
import com.youxuan.merchant.model.StoreDO;
import com.youxuan.merchant.repository.ProductRepository;
import com.youxuan.merchant.repository.StoreRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家商品管理服务。
 * 商家模块直接操作共享的商品数据库，仅管理归属于本商家的商品。
 */
@Service
public class MerchantProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final IdGenerator idGenerator;

    public MerchantProductService(ProductRepository productRepository,
                                  StoreRepository storeRepository,
                                  IdGenerator idGenerator) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 分页查询商家自己的商品列表。
     *
     * @param merchantId  商家 ID
     * @param keyword     搜索关键词（可选）
     * @param auditStatus 审核状态筛选（可选）
     * @param dateFrom    创建日期起始（可选）
     * @param dateTo      创建日期截止（可选）
     * @param pageNo      页码
     * @param pageSize    每页条数
     * @return 商品列表
     */
    public List<ProductDO> listProducts(Long merchantId, String keyword, String auditStatus, String dateFrom, String dateTo, int pageNo, int pageSize) {
        return productRepository.findByMerchantId(merchantId, keyword, auditStatus, dateFrom, dateTo, pageNo, pageSize);
    }

    /**
     * 统计商家商品总数。
     *
     * @param merchantId  商家 ID
     * @param keyword     搜索关键词（可选）
     * @param auditStatus 审核状态筛选（可选）
     * @param dateFrom    创建日期起始（可选）
     * @param dateTo      创建日期截止（可选）
     * @return 商品总数
     */
    public long countProducts(Long merchantId, String keyword, String auditStatus, String dateFrom, String dateTo) {
        return productRepository.countByMerchantId(merchantId, keyword, auditStatus, dateFrom, dateTo);
    }

    /**
     * 创建商品。
     *
     * @param merchantId 商家 ID
     * @param request    创建商品请求
     * @return 商品对象
     */
    @Transactional
    public ProductDO createProduct(Long merchantId, CreateProductRequest request) {
        StoreDO store = storeRepository.findByMerchantId(merchantId);
        if (store == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "店铺信息不存在，请先完成入驻");
        }

        ProductDO product = new ProductDO();
        product.setId(idGenerator.nextId());
        product.setProductNo("P" + product.getId());
        product.setMerchantId(merchantId);
        product.setStoreId(store.getId());
        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName());
        product.setMainImageUrl(request.getMainImageUrl());
        product.setDetailHtml(request.getDetailHtml());
        product.setPrice(request.getPrice());
        product.setStockTotal(request.getStockTotal());
        product.setAuditStatus("PENDING");
        product.setSaleStatus("OFF_SALE");
        product.setRemark(request.getRemark());
        productRepository.insert(product);
        return product;
    }

    /**
     * 上架商品。
     *
     * @param merchantId 商家 ID
     * @param productId  商品 ID
     */
    @Transactional
    public void onSale(Long merchantId, Long productId) {
        ProductDO product = productRepository.findById(productId);
        if (product == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作该商品");
        }
        if (!"APPROVED".equals(product.getAuditStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "商品未通过审核，无法上架");
        }
        productRepository.updateSaleStatus(productId, "ON_SALE");
    }

    /**
     * 下架商品。
     *
     * @param merchantId 商家 ID
     * @param productId  商品 ID
     */
    @Transactional
    public void offSale(Long merchantId, Long productId) {
        ProductDO product = productRepository.findById(productId);
        if (product == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        if (!product.getMerchantId().equals(merchantId)) {
            throw new BizException(ErrorCode.PERMISSION_DENIED, "无权操作该商品");
        }
        productRepository.updateSaleStatus(productId, "OFF_SALE");
    }
}
