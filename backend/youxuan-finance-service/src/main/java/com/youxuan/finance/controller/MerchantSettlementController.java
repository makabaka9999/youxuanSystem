package com.youxuan.finance.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.finance.model.SettlementOrderDO;
import com.youxuan.finance.service.SettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户结算单控制器（MerchantSettlementController）。
 * <p>
 * 提供商户端结算单的查询接口，商户可查看结算周期内的
 * 结算汇总信息和结算单详情。
 * 接口路径：/api/v1/merchant/settlements
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/merchant/settlements")
public class MerchantSettlementController {

    private final SettlementService settlementService;

    public MerchantSettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    /**
     * 分页查询当前商户的结算单列表。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结算单列表
     */
    @GetMapping
    public ApiResponse<PageResponse<SettlementOrderDO>> listSettlements(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long merchantId = JwtRequestContext.get() != null ? JwtRequestContext.get().getMerchantId() : null;
        if (merchantId == null) {
            return ApiResponse.error("UNAUTHORIZED", "无法获取商户信息");
        }
        PageResponse<SettlementOrderDO> result = settlementService.listSettlements(merchantId, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }

    /**
     * 查询结算单详情。
     *
     * @param id 结算单 ID
     * @return 结算单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<SettlementOrderDO> getSettlement(@PathVariable Long id) {
        SettlementOrderDO settlement = settlementService.getSettlementById(id);
        if (settlement == null) {
            return ApiResponse.error("NOT_FOUND", "结算单不存在");
        }
        return ApiResponse.success(settlement, RequestContext.getRequestId());
    }
}
