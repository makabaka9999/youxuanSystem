package com.youxuan.admin.controller;

import com.youxuan.admin.model.ProductAuditVO;
import com.youxuan.admin.service.AdminProductService;
import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台后台 - 商品管理控制器（AdminProductController）。
 * <p>
 * 提供商品查询和审核操作接口。
 * 接口路径：/api/v1/admin/products
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/products")
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    /**
     * 分页查询商品列表，可按审核状态筛选。
     *
     * @param keyword     搜索关键词
     * @param merchantId  商户 ID（可选）
     * @param auditStatus 审核状态：PENDING / APPROVED / REJECTED，不传返回全部
     * @param pageNo      页码
     * @param pageSize    每页条数
     */
    @GetMapping
    public ApiResponse<PageResponse<ProductAuditVO>> listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<ProductAuditVO> result =
                adminProductService.listProducts(keyword, merchantId, auditStatus, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 分页查询待审核商品列表。
     */
    @GetMapping("/pending-audit")
    public ApiResponse<PageResponse<ProductAuditVO>> listPendingAuditProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<ProductAuditVO> result =
                adminProductService.listProducts(keyword, merchantId, "PENDING", pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 审核商品。
     *
     * @param productId 商品 ID
     * @param request   审核请求（包含 approved 和 reason）
     * @return 操作结果
     */
    @PostMapping("/{productId}/audit")
    public ApiResponse<Void> auditProduct(@PathVariable Long productId, @RequestBody Map<String, Object> request) {
        boolean approved = Boolean.TRUE.equals(request.get("approved"));
        String reason = (String) request.getOrDefault("reason", "");
        adminProductService.auditProduct(productId, approved, reason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
