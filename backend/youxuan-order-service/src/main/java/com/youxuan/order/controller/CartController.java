package com.youxuan.order.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.security.JwtClaims;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.dto.AddCartItemRequest;
import com.youxuan.order.dto.BatchCheckRequest;
import com.youxuan.order.dto.UpdateCartQuantityRequest;
import com.youxuan.order.model.CartDO;
import com.youxuan.order.service.CartService;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物车控制器
 * <p>
 * 提供购物车列表查询、添加商品、修改数量、删除商品及批量选中/取消选中等接口。
 * 所有接口需要用户登录认证（通过 JWT 获取用户信息）。
 * </p>
 */
@RestController
@RequestMapping("/api/v1/carts")
public class CartController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    /**
     * 获取当前用户的购物车列表
     *
     * @return 购物车项列表
     */
    @GetMapping
    public ApiResponse<List<CartDO>> getCart() {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("查询购物车列表. userId={}", userId);
        List<CartDO> cartList = cartService.getCart(userId);
        return ApiResponse.success(cartList, RequestContext.getRequestId());
    }

    /**
     * 添加商品到购物车
     * <p>
     * 如果购物车中已有同一 SKU，则累加数量；否则新增一条记录。
     * </p>
     *
     * @param request 添加购物车请求体
     * @return 购物车项
     */
    @PostMapping("/items")
    public ApiResponse<CartDO> addItem(@Valid @RequestBody AddCartItemRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("添加购物车. userId={}, skuId={}, quantity={}", userId, request.getSkuId(), request.getQuantity());
        CartDO cart = cartService.addItem(userId, request.getStoreId(), request.getProductId(),
                request.getSkuId(), request.getQuantity());
        return ApiResponse.success(cart, RequestContext.getRequestId());
    }

    /**
     * 更新购物车项数量
     *
     * @param itemId  购物车项ID
     * @param request 更新数量请求体
     * @return 操作结果
     */
    @PutMapping("/items/{itemId}")
    public ApiResponse<Void> updateQuantity(@PathVariable Long itemId,
                                             @Valid @RequestBody UpdateCartQuantityRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("更新购物车数量. itemId={}, quantity={}", itemId, request.getQuantity());
        cartService.updateQuantity(userId, itemId, request.getQuantity());
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 删除购物车项
     *
     * @param itemId 购物车项ID
     * @return 操作结果
     */
    @DeleteMapping("/items/{itemId}")
    public ApiResponse<Void> removeItem(@PathVariable Long itemId) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("删除购物车项. itemId={}", itemId);
        cartService.removeItem(userId, itemId);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 批量更新购物车项选中状态
     *
     * @param request 批量选中请求体
     * @return 操作结果
     */
    @PostMapping("/items/check")
    public ApiResponse<Void> batchCheck(@Valid @RequestBody BatchCheckRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("批量更新购物车选中状态. itemIds={}, checked={}", request.getItemIds(), request.getChecked());
        cartService.batchCheck(userId, request.getItemIds(), request.getChecked());
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
