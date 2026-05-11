package com.youxuan.admin.controller;

import com.youxuan.admin.model.OperationLogDO;
import com.youxuan.admin.service.AdminLogService;
import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台后台 - 操作日志查询控制器（AdminLogController）。
 * <p>
 * 提供操作审计日志的查询接口，平台管理员可按模块、操作等
 * 维度检索操作记录，用于安全审计和问题追溯。
 * 接口路径：/api/v1/admin/operation-logs
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/operation-logs")
public class AdminLogController {

    private final AdminLogService adminLogService;

    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    /**
     * 分页查询操作日志。
     *
     * @param moduleCode 模块编码（可选）
     * @param actionCode 操作编码（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 操作日志分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<OperationLogDO>> queryLogs(
            @RequestParam(required = false) String moduleCode,
            @RequestParam(required = false) String actionCode,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<OperationLogDO> result = adminLogService.queryLogs(moduleCode, actionCode, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }
}
