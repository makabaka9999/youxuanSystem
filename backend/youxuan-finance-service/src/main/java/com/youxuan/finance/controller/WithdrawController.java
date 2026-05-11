package com.youxuan.finance.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import com.youxuan.finance.service.WithdrawService;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提现控制器（WithdrawController）。
 * <p>
 * 提供商户端提现申请接口，商户可基于已审核的结算单发起提现。
 * 接口路径：/api/v1/merchant/withdraw-orders
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/merchant/withdraw-orders")
public class WithdrawController {

    private final WithdrawService withdrawService;

    public WithdrawController(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    /**
     * 商户发起提现申请（存根方法）。
     * <p>
     * 当前为简化实现，仅返回成功响应，实际业务需校验
     * 结算单状态、商户余额等前置条件并记录提现单。
     * </p>
     *
     * @param request 提现请求参数（包含 settlementId、amount 等）
     * @return 操作结果
     */
    @PostMapping
    public ApiResponse<Void> applyWithdraw(@RequestBody Map<String, Object> request) {
        // 存根实现：后续接入完整的前置校验和幂等性处理
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
