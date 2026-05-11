package com.youxuan.admin.controller;

import com.youxuan.admin.service.AdminOrderService;
import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台后台 - 订单管理控制器（AdminOrderController）。
 * <p>
 * 提供平台维度的订单查询和详情查看接口。
 * 接口路径：/api/v1/admin/orders
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    /**
     * 分页查询订单列表。
     *
     * @param keyword    搜索关键词
     * @param merchantId 商户 ID（可选）
     * @param status     订单状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 订单分页列表
     */
    @GetMapping
    public ApiResponse<PageResponse<Map<String, Object>>> listOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResponse<Map<String, Object>> result = adminOrderService.listOrders(keyword, merchantId, status, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 获取订单详情。
     *
     * @param orderId 订单 ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public ApiResponse<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        Map<String, Object> detail = adminOrderService.getOrderDetail(orderId);
        return ApiResponse.success(detail, RequestContext.getRequestId());
    }
}
