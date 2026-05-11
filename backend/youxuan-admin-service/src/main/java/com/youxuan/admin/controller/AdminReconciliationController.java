package com.youxuan.admin.controller;

import com.youxuan.admin.service.AdminReconciliationService;
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
 * 平台后台 - 对账管理控制器（AdminReconciliationController）。
 * <p>
 * 提供对账记录的查询和差异处理操作接口。
 * 接口路径：/api/v1/admin/reconciliations
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/reconciliations")
public class AdminReconciliationController {

    private final AdminReconciliationService adminReconciliationService;

    public AdminReconciliationController(AdminReconciliationService adminReconciliationService) {
        this.adminReconciliationService = adminReconciliationService;
    }

    /**
     * 分页查询对账记录列表。
     *
     * @param channel 支付渠道（可选）
     * @param status  处理状态（可选）
     * @param pageNo  页码
     * @param pageSize 每页条数
     * @return 对账记录分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listReconciliations(
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<Map<String, Object>> result =
                adminReconciliationService.listReconciliations(channel, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 处理对账差异。
     *
     * @param id       对账记录 ID
     * @param request  处理请求（包含 handleResult 和 resolved）
     * @return 操作结果
     */
    @PostMapping("/{id}/resolve")
    public ApiResponse<Void> resolveDiff(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String handleResult = (String) request.getOrDefault("handleResult", "");
        boolean resolved = Boolean.TRUE.equals(request.get("resolved"));
        adminReconciliationService.resolveDiff(id, handleResult, resolved);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
