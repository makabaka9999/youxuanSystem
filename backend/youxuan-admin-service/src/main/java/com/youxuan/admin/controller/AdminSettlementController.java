package com.youxuan.admin.controller;

import com.youxuan.admin.service.AdminSettlementService;
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
 * 平台后台 - 结算管理控制器（AdminSettlementController）。
 * <p>
 * 提供结算单的查询和审核操作接口。
 * 接口路径：/api/v1/admin/settlements
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/settlements")
public class AdminSettlementController {

    private final AdminSettlementService adminSettlementService;

    public AdminSettlementController(AdminSettlementService adminSettlementService) {
        this.adminSettlementService = adminSettlementService;
    }

    /**
     * 分页查询结算单列表。
     *
     * @param merchantId 商户 ID（可选）
     * @param status     结算单状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 结算单分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listSettlements(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<Map<String, Object>> result =
                adminSettlementService.listSettlements(merchantId, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 审核结算单。
     *
     * @param id       结算单 ID
     * @param request  审核请求（包含 approved 和 auditReason）
     * @return 操作结果
     */
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> auditSettlement(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        boolean approved = Boolean.TRUE.equals(request.get("approved"));
        String auditReason = (String) request.getOrDefault("auditReason", "");
        adminSettlementService.auditSettlement(id, approved, auditReason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
