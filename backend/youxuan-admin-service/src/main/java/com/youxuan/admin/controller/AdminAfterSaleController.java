package com.youxuan.admin.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台后台 - 售后管理控制器（AdminAfterSaleController）。
 * <p>
 * 提供平台维度的售后单查询和平台介入处理接口。
 * 当商户与用户就售后问题无法达成一致时，平台可介入处理。
 * 接口路径：/api/v1/admin/after-sales
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/after-sales")
public class AdminAfterSaleController {

    /**
     * 分页查询售后单列表。
     *
     * @param status   售后状态（可选）
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 售后单分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listAfterSales(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        // 实际业务中调用 order-service 的售后查询接口
        List<Map<String, Object>> list = new ArrayList<>();
        PageResponse<Map<String, Object>> result = new PageResponse<>(pageNo, pageSize, 0, list);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 获取售后单详情。
     *
     * @param afterSaleId 售后单 ID
     * @return 售后单详情
     */
    @GetMapping("/{afterSaleId}")
    public ApiResponse<Map<String, Object>> getAfterSaleDetail(@PathVariable Long afterSaleId) {
        // 实际业务中调用 order-service 的售后详情接口
        return ApiResponse.success(new LinkedHashMap<>(), RequestContext.getRequestId());
    }

    /**
     * 平台介入处理售后单。
     *
     * @param afterSaleId 售后单 ID
     * @param request     处理请求（包含 judgment、reason 等）
     * @return 操作结果
     */
    @PostMapping("/{afterSaleId}/intervene")
    public ApiResponse<Void> interveneAfterSale(@PathVariable Long afterSaleId, @RequestBody Map<String, Object> request) {
        // 实际业务中调用 order-service 的售后介入接口
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
