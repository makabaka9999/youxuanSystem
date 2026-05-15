package com.youxuan.merchant.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.merchant.dto.DashboardDTO;
import com.youxuan.merchant.dto.MerchantApplicationRequest;
import com.youxuan.merchant.dto.CreateProductRequest;
import com.youxuan.merchant.model.MerchantApplicationDO;
import com.youxuan.merchant.model.ProductDO;
import com.youxuan.merchant.service.MerchantService;
import com.youxuan.merchant.service.MerchantProductService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家入驻及商家端核心接口。
 */
@RestController
@RequestMapping("/api/v1")
public class MerchantController {

    private final MerchantService merchantService;
    private final MerchantProductService merchantProductService;

    public MerchantController(MerchantService merchantService,
                              MerchantProductService merchantProductService) {
        this.merchantService = merchantService;
        this.merchantProductService = merchantProductService;
    }

    /**
     * 提交入驻申请。
     *
     * @param request 入驻申请信息
     * @return 入驻申请结果
     */
    @PostMapping("/merchant-applications")
    public ApiResponse<MerchantApplicationDO> apply(@RequestBody MerchantApplicationRequest request) {
        Long userId = JwtRequestContext.get().getUserId();
        MerchantApplicationDO result = merchantService.apply(userId, request);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 查询商家工作台仪表盘数据。
     *
     * @return 仪表盘数据
     */
    @GetMapping("/merchant/dashboard")
    public ApiResponse<DashboardDTO> getDashboard() {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        DashboardDTO dashboard = merchantService.getDashboard(merchantId);
        return ApiResponse.success(dashboard, RequestContext.getRequestId());
    }

    /**
     * 分页查询商家自己的商品列表（支持名称搜索、审核状态筛选、日期范围）。
     *
     * @param keyword     搜索关键词（可选）
     * @param auditStatus 审核状态：PENDING / APPROVED / REJECTED（可选）
     * @param dateFrom    创建日期起始 YYYY-MM-DD（可选）
     * @param dateTo      创建日期截止 YYYY-MM-DD（可选）
     * @param pageNo      页码
     * @param pageSize    每页条数
     * @return 商品分页列表（按创建时间倒序）
     */
    @GetMapping("/merchant/products")
    public ApiResponse<PageResponse<ProductDO>> listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        if (pageSize > 100) pageSize = 100;
        if (pageNo < 1) pageNo = 1;
        List<ProductDO> items = merchantProductService.listProducts(merchantId, keyword, auditStatus, dateFrom, dateTo, pageNo, pageSize);
        long total = merchantProductService.countProducts(merchantId, keyword, auditStatus, dateFrom, dateTo);
        return ApiResponse.success(new PageResponse<>(pageNo, pageSize, total, items), RequestContext.getRequestId());
    }

    /**
     * 商家创建商品。
     *
     * @param request 商品信息
     * @return 创建的商品
     */
    @PostMapping("/merchant/products")
    public ApiResponse<ProductDO> createProduct(@RequestBody CreateProductRequest request) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        ProductDO product = merchantProductService.createProduct(merchantId, request);
        return ApiResponse.success(product, RequestContext.getRequestId());
    }

    /**
     * 上架商品。
     *
     * @param productId 商品 ID
     * @return 操作结果
     */
    @PostMapping("/merchant/products/{productId}/on-sale")
    public ApiResponse<Void> onSale(@PathVariable Long productId) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        merchantProductService.onSale(merchantId, productId);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 下架商品。
     *
     * @param productId 商品 ID
     * @return 操作结果
     */
    @PostMapping("/merchant/products/{productId}/off-sale")
    public ApiResponse<Void> offSale(@PathVariable Long productId) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        merchantProductService.offSale(merchantId, productId);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 编辑商品信息。
     *
     * @param productId 商品 ID
     * @param request   商品信息
     * @return 操作结果
     */
    @PutMapping("/merchant/products/{productId}")
    public ApiResponse<Void> updateProduct(@PathVariable Long productId, @RequestBody CreateProductRequest request) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        merchantProductService.updateProduct(merchantId, productId, request);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
