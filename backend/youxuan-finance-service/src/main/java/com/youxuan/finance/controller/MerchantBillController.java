package com.youxuan.finance.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.finance.model.MerchantBillDO;
import com.youxuan.finance.service.BillService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户账单控制器（MerchantBillController）。
 * <p>
 * 提供商户端账单查询接口，商户可查看自己名下的每日账单汇总，
 * 包括订单金额、退款金额、佣金及应付金额等信息。
 * 接口路径：/api/v1/merchant/bills
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/merchant/bills")
public class MerchantBillController {

    private final BillService billService;

    public MerchantBillController(BillService billService) {
        this.billService = billService;
    }

    /**
     * 分页查询当前商户的账单列表。
     * <p>
     * 从 JWT 上下文中获取当前商户 ID，查询其名下的账单记录，
     * 按账单日期倒序排列。
     * </p>
     *
     * @param pageNo   页码（从 1 开始，默认 1）
     * @param pageSize 每页条数（默认 20）
     * @return 分页账单列表
     */
    @GetMapping
    public ApiResponse<PageResponse<MerchantBillDO>> listBills(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {
        Long merchantId = JwtRequestContext.get() != null ? JwtRequestContext.get().getMerchantId() : null;
        if (merchantId == null) {
            return ApiResponse.error("UNAUTHORIZED", "无法获取商户信息");
        }
        PageResponse<MerchantBillDO> result = billService.listBills(merchantId, pageNo, pageSize);
        return ApiResponse.success(result, RequestContext.getRequestId());
    }
}
