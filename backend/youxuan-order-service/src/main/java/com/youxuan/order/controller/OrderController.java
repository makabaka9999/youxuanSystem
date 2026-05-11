package com.youxuan.order.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.security.JwtClaims;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.dto.OrderCreateRequest;
import com.youxuan.order.model.OrderDO;
import com.youxuan.order.service.OrderService;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制器
 * <p>
 * 提供用户端订单的创建、列表查询、详情查看、取消和确认收货等接口，
 * 以及商家端订单列表查询接口。
 * 所有接口需要登录认证（通过 JWT 获取用户/商家信息）。
 * </p>
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * <p>
     * 用户提交下单请求，系统根据商品所属商家自动拆分订单。
     * </p>
     *
     * @param request 创建订单请求体
     * @return 创建的订单列表
     */
    @PostMapping
    public ApiResponse<List<OrderDO>> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("创建订单. userId={}, itemsCount={}", userId, request.getItems().size());
        List<OrderDO> orders = orderService.createOrder(userId, request.getAddressId(),
                request.getItems(), request.getRemark());
        return ApiResponse.success(orders, RequestContext.getRequestId());
    }

    /**
     * 分页查询当前用户的订单列表
     *
     * @param status  订单状态（可选，为空则查询全部）
     * @param pageNo  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResponse<OrderDO>> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("查询订单列表. userId={}, status={}, pageNo={}, pageSize={}", userId, status, pageNo, pageSize);
        PageResponse<OrderDO> page = orderService.getOrders(userId, status, pageNo, pageSize);
        return ApiResponse.success(page, RequestContext.getRequestId());
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单对象
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderDO> orderDetail(@PathVariable Long orderId) {
        JwtClaims claims = JwtRequestContext.get();
        LOGGER.info("查询订单详情. orderId={}", orderId);
        OrderDO order = orderService.getOrderDetail(orderId);
        return ApiResponse.success(order, RequestContext.getRequestId());
    }

    /**
     * 取消订单
     * <p>
     * 仅待支付状态的订单可取消。
     * </p>
     *
     * @param orderId 订单ID
     * @param body    请求体（含 cancelReason）
     * @return 操作结果
     */
    @PostMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        String reason = body.getOrDefault("cancelReason", "用户取消");
        LOGGER.info("取消订单. orderId={}, reason={}", orderId, reason);
        orderService.cancelOrder(userId, orderId, reason);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 确认收货
     * <p>
     * 仅已发货状态的订单可确认收货。
     * </p>
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PostMapping("/{orderId}/confirm-receipt")
    public ApiResponse<Void> confirmReceipt(@PathVariable Long orderId) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("确认收货. orderId={}", orderId);
        orderService.confirmReceipt(userId, orderId);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    // ========== 商家端订单接口 ==========

    /**
     * 商家分页查询自己店铺的订单列表
     *
     * @param pageNo   页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页结果
     */
    @GetMapping("/merchant")
    public ApiResponse<PageResponse<OrderDO>> merchantOrders(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        JwtClaims claims = JwtRequestContext.get();
        Long merchantId = claims.getMerchantId();
        if (merchantId == null) {
            return ApiResponse.fail(com.youxuan.common.api.ErrorCode.PERMISSION_DENIED.getCode(),
                    "非商家用户", RequestContext.getRequestId());
        }
        LOGGER.info("商家查询订单列表. merchantId={}, pageNo={}, pageSize={}", merchantId, pageNo, pageSize);
        PageResponse<OrderDO> page = orderService.getMerchantOrders(merchantId, pageNo, pageSize);
        return ApiResponse.success(page, RequestContext.getRequestId());
    }
}
