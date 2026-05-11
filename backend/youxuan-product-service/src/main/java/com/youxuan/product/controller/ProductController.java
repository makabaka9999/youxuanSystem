package com.youxuan.product.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.product.model.ProductDO;
import com.youxuan.product.service.ProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品查询接口。
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 可售商品列表，支持关键词搜索和类目筛选。
     *
     * @param keyword    搜索关键词
     * @param categoryId 类目 ID
     * @param pageNo     页码，默认 1
     * @param pageSize   每页条数，默认 20，最大 100
     * @return 分页商品列表
     */
    @GetMapping
    public ApiResponse<PageResponse<ProductDO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {

        if (pageSize > 100) pageSize = 100;
        if (pageNo < 1) pageNo = 1;

        List<ProductDO> items = productService.listOnSaleProducts(keyword, categoryId, pageNo, pageSize);
        int total = productService.countOnSaleProducts(keyword, categoryId);

        return ApiResponse.success(new PageResponse<>(pageNo, pageSize, total, items));
    }

    /**
     * 商品详情。
     *
     * @param productId 商品 ID
     * @return 商品信息
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductDO> detail(@PathVariable Long productId) {
        ProductDO product = productService.getProductById(productId);
        if (product == null) {
            return ApiResponse.error("RESOURCE_NOT_FOUND", "商品不存在");
        }
        return ApiResponse.success(product);
    }
}
