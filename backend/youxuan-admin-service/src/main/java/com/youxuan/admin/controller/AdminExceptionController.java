package com.youxuan.admin.controller;

import com.youxuan.admin.model.ExceptionOrderDO;
import com.youxuan.admin.service.AdminExceptionService;
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
 * 平台后台 - 异常管理控制器（AdminExceptionController）。
 * <p>
 * 提供异常单的查询、处理和解决操作接口。
 * 平台运营人员通过此控制器管理各业务环节产生的异常。
 * 接口路径：/api/v1/admin/exceptions
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/exceptions")
public class AdminExceptionController {

    private final AdminExceptionService adminExceptionService;

    public AdminExceptionController(AdminExceptionService adminExceptionService) {
        this.adminExceptionService = adminExceptionService;
    }

    /**
     * 分页查询异常单列表。
     *
     * @param exceptionType 异常类型（可选）
     * @param status        处理状态（可选）
     * @param pageNo        页码
     * @param pageSize      每页条数
     * @return 异常单分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<ExceptionOrderDO>> listExceptions(
            @RequestParam(required = false) String exceptionType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<ExceptionOrderDO> result =
                adminExceptionService.listExceptions(exceptionType, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 获取异常单详情。
     *
     * @param id 异常单 ID
     * @return 异常单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ExceptionOrderDO> getException(@PathVariable Long id) {
        ExceptionOrderDO exception = adminExceptionService.getExceptionById(id);
        if (exception == null) {
            return ApiResponse.error("NOT_FOUND", "异常单不存在");
        }
        return ApiResponse.success(exception, RequestContext.getRequestId());
    }

    /**
     * 处理异常单（标记为处理中）。
     *
     * @param id      异常单 ID
     * @param request 处理请求（包含 handleResult）
     * @return 操作结果
     */
    @PostMapping("/{id}/process")
    public ApiResponse<Void> processException(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String handleResult = (String) request.getOrDefault("handleResult", "");
        adminExceptionService.processException(id, handleResult);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 解决异常单（标记为已解决）。
     *
     * @param id      异常单 ID
     * @param request 解决请求（包含 handleResult）
     * @return 操作结果
     */
    @PostMapping("/{id}/resolve")
    public ApiResponse<Void> resolveException(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String handleResult = (String) request.getOrDefault("handleResult", "");
        adminExceptionService.resolveException(id, handleResult);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
