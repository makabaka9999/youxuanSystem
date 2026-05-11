package com.youxuan.merchant.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.merchant.dto.StoreUpdateRequest;
import com.youxuan.merchant.model.StoreDO;
import com.youxuan.merchant.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家店铺管理接口。
 */
@RestController
@RequestMapping("/api/v1/merchant/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * 查询当前商家的店铺信息。
     *
     * @return 店铺信息
     */
    @GetMapping
    public ApiResponse<StoreDO> getStore() {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        StoreDO store = storeService.getStore(merchantId);
        return ApiResponse.success(store, RequestContext.getRequestId());
    }

    /**
     * 更新店铺信息。
     *
     * @param request 店铺更新信息
     * @return 操作结果
     */
    @PutMapping
    public ApiResponse<Void> updateStore(@RequestBody StoreUpdateRequest request) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        storeService.updateStore(merchantId, request);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
