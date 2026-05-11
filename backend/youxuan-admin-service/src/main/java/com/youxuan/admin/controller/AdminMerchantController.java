package com.youxuan.admin.controller;

import com.youxuan.admin.service.AdminMerchantService;
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
 * 平台后台 - 商户管理控制器（AdminMerchantController）。
 * <p>
 * 提供商户列表查询、入驻审核、冻结/解冻等管理接口。
 * 接口路径：/api/v1/admin/merchants
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/merchants")
public class AdminMerchantController {

    private final AdminMerchantService adminMerchantService;

    public AdminMerchantController(AdminMerchantService adminMerchantService) {
        this.adminMerchantService = adminMerchantService;
    }

    /**
     * 分页查询商户列表。
     *
     * @param keyword  搜索关键词
     * @param status   商户状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 商户分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listMerchants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<Map<String, Object>> result = adminMerchantService.listMerchants(keyword, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 审核商户入驻申请。
     *
     * @param merchantId 商户 ID
     * @param request    审核请求（包含 approved 和 reason）
     * @return 操作结果
     */
    @PostMapping("/{merchantId}/audit")
    public ApiResponse<Void> auditMerchant(@PathVariable Long merchantId, @RequestBody Map<String, Object> request) {
        boolean approved = Boolean.TRUE.equals(request.get("approved"));
        String reason = (String) request.getOrDefault("reason", "");
        adminMerchantService.auditMerchant(merchantId, approved, reason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 冻结商户账户。
     *
     * @param merchantId 商户 ID
     * @param request    请求参数（包含 reason）
     * @return 操作结果
     */
    @PostMapping("/{merchantId}/freeze")
    public ApiResponse<Void> freezeMerchant(@PathVariable Long merchantId, @RequestBody Map<String, Object> request) {
        String reason = (String) request.getOrDefault("reason", "");
        adminMerchantService.freezeMerchant(merchantId, reason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 解冻商户账户。
     *
     * @param merchantId 商户 ID
     * @param request    请求参数（包含 reason）
     * @return 操作结果
     */
    @PostMapping("/{merchantId}/unfreeze")
    public ApiResponse<Void> unfreezeMerchant(@PathVariable Long merchantId, @RequestBody Map<String, Object> request) {
        String reason = (String) request.getOrDefault("reason", "");
        adminMerchantService.unfreezeMerchant(merchantId, reason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
