package com.youxuan.admin.controller;

import com.youxuan.admin.service.AdminWithdrawService;
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
 * 平台后台 - 提现管理控制器（AdminWithdrawController）。
 * <p>
 * 提供提现单的查询、审核和支付状态更新操作接口。
 * 接口路径：/api/v1/admin/withdraw-orders
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/withdraw-orders")
public class AdminWithdrawController {

    private final AdminWithdrawService adminWithdrawService;

    public AdminWithdrawController(AdminWithdrawService adminWithdrawService) {
        this.adminWithdrawService = adminWithdrawService;
    }

    /**
     * 分页查询提现单列表。
     *
     * @param merchantId 商户 ID（可选）
     * @param status     提现单状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 提现单分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listWithdraws(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<Map<String, Object>> result =
                adminWithdrawService.listWithdraws(merchantId, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 审核提现单。
     *
     * @param id       提现单 ID
     * @param request  审核请求（包含 approved 和 auditReason）
     * @return 操作结果
     */
    @PostMapping("/{id}/audit")
    public ApiResponse<Void> auditWithdraw(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        boolean approved = Boolean.TRUE.equals(request.get("approved"));
        String auditReason = (String) request.getOrDefault("auditReason", "");
        adminWithdrawService.auditWithdraw(id, approved, auditReason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 更新提现单支付状态。
     *
     * @param id       提现单 ID
     * @param request  支付结果请求（包含 paySuccess 和 failReason）
     * @return 操作结果
     */
    @PostMapping("/{id}/pay-status")
    public ApiResponse<Void> updatePayStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        boolean paySuccess = Boolean.TRUE.equals(request.get("paySuccess"));
        String failReason = (String) request.getOrDefault("failReason", "");
        adminWithdrawService.updatePayStatus(id, paySuccess, failReason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
