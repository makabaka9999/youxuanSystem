package com.youxuan.merchant.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.merchant.dto.CreateSettlementAccountRequest;
import com.youxuan.merchant.model.SettlementAccountDO;
import com.youxuan.merchant.service.SettlementAccountService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家结算账户管理接口。
 */
@RestController
@RequestMapping("/api/v1/merchant/settlement-accounts")
public class SettlementAccountController {

    private final SettlementAccountService settlementAccountService;

    public SettlementAccountController(SettlementAccountService settlementAccountService) {
        this.settlementAccountService = settlementAccountService;
    }

    /**
     * 查询当前商家的所有结算账户。
     *
     * @return 结算账户列表
     */
    @GetMapping
    public ApiResponse<List<SettlementAccountDO>> listAccounts() {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        List<SettlementAccountDO> accounts = settlementAccountService.listAccounts(merchantId);
        return ApiResponse.success(accounts, RequestContext.getRequestId());
    }

    /**
     * 创建结算账户。
     *
     * @param request 结算账户信息
     * @return 创建成功的结算账户
     */
    @PostMapping
    public ApiResponse<SettlementAccountDO> createAccount(@RequestBody CreateSettlementAccountRequest request) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        SettlementAccountDO account = settlementAccountService.createAccount(merchantId, request);
        return ApiResponse.success(account, RequestContext.getRequestId());
    }
}
