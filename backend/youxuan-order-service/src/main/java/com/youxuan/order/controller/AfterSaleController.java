package com.youxuan.order.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.security.JwtClaims;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.dto.AfterSaleCreateRequest;
import com.youxuan.order.dto.MerchantDecisionRequest;
import com.youxuan.order.dto.PlatformInterveneRequest;
import com.youxuan.order.dto.ReturnShipmentSubmitRequest;
import com.youxuan.order.model.AfterSaleDO;
import com.youxuan.order.service.AfterSaleService;
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
 * 售后控制器
 * <p>
 * 提供用户端售后申请的创建、查询，以及商家端售后审核和平台端售后介入等接口。
 * 所有接口需要登录认证（通过 JWT 获取用户/商家/平台管理员信息）。
 * </p>
 */
@RestController
@RequestMapping("/api/v1/after-sales")
public class AfterSaleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterSaleController.class);

    @Autowired
    private AfterSaleService afterSaleService;

    /**
     * 创建售后申请
     * <p>
     * 用户对已支付订单中的商品发起退款或退货退款申请。
     * </p>
     *
     * @param request 创建售后请求体
     * @return 创建的售后单
     */
    @PostMapping
    public ApiResponse<AfterSaleDO> createAfterSale(@Valid @RequestBody AfterSaleCreateRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("创建售后申请. userId={}, orderId={}, type={}", userId, request.getOrderId(), request.getType());
        AfterSaleDO afterSale = afterSaleService.createAfterSale(userId, request);
        return ApiResponse.success(afterSale, RequestContext.getRequestId());
    }

    /**
     * 分页查询当前用户的售后单列表
     *
     * @param pageNo   页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResponse<AfterSaleDO>> listAfterSales(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("查询售后单列表. userId={}, pageNo={}, pageSize={}", userId, pageNo, pageSize);
        PageResponse<AfterSaleDO> page = afterSaleService.getAfterSales(userId, pageNo, pageSize);
        return ApiResponse.success(page, RequestContext.getRequestId());
    }

    /**
     * 获取售后单详情
     *
     * @param afterSaleId 售后单ID
     * @return 售后单对象
     */
    @GetMapping("/{afterSaleId}")
    public ApiResponse<AfterSaleDO> afterSaleDetail(@PathVariable Long afterSaleId) {
        LOGGER.info("查询售后单详情. afterSaleId={}", afterSaleId);
        AfterSaleDO afterSale = afterSaleService.getAfterSaleDetail(afterSaleId);
        return ApiResponse.success(afterSale, RequestContext.getRequestId());
    }

    /**
     * 提交退货物流信息
     * <p>
     * 用户在商家同意退货退款后，填写退货运单信息。
     * </p>
     *
     * @param request 提交退货物流请求体
     * @return 操作结果
     */
    @PostMapping("/return-shipment")
    public ApiResponse<Void> submitReturnShipment(@Valid @RequestBody ReturnShipmentSubmitRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("提交退货物流. userId={}, afterSaleId={}, logisticsCompany={}, trackingNo={}",
                userId, request.getAfterSaleId(), request.getLogisticsCompany(), request.getTrackingNo());
        afterSaleService.submitReturnShipment(userId, request.getAfterSaleId(),
                request.getLogisticsCompany(), request.getTrackingNo());
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    // ========== 商家端售后接口 ==========

    /**
     * 商家处理售后申请
     *
     * @param request 商家处理请求体
     * @return 操作结果
     */
    @PostMapping("/merchant-decision")
    public ApiResponse<Void> merchantDecision(@Valid @RequestBody MerchantDecisionRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long merchantId = claims.getMerchantId();
        if (merchantId == null) {
            return ApiResponse.fail(com.youxuan.common.api.ErrorCode.PERMISSION_DENIED.getCode(),
                    "非商家用户", RequestContext.getRequestId());
        }
        LOGGER.info("商家处理售后. merchantId={}, afterSaleId={}, action={}", merchantId, request.getAfterSaleId(), request.getAction());
        afterSaleService.handleMerchantDecision(merchantId, request.getAfterSaleId(),
                request.getAction(), request.getAmount(), request.getReason());
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    // ========== 平台端售后接口 ==========

    /**
     * 平台介入处理售后
     *
     * @param request 平台介入请求体
     * @return 操作结果
     */
    @PostMapping("/platform-intervene")
    public ApiResponse<Void> platformIntervene(@Valid @RequestBody PlatformInterveneRequest request) {
        LOGGER.info("平台介入售后. afterSaleId={}, decision={}", request.getAfterSaleId(), request.getDecision());
        afterSaleService.platformIntervene(request.getAfterSaleId(), request.getDecision(),
                request.getAmount(), request.getReason());
        return ApiResponse.success(null, RequestContext.getRequestId());
    }

    /**
     * 商家分页查询售后单列表
     *
     * @param pageNo   页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页结果
     */
    @GetMapping("/merchant")
    public ApiResponse<PageResponse<AfterSaleDO>> merchantAfterSales(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        JwtClaims claims = JwtRequestContext.get();
        Long merchantId = claims.getMerchantId();
        if (merchantId == null) {
            return ApiResponse.fail(com.youxuan.common.api.ErrorCode.PERMISSION_DENIED.getCode(),
                    "非商家用户", RequestContext.getRequestId());
        }
        LOGGER.info("商家查询售后单列表. merchantId={}, pageNo={}, pageSize={}", merchantId, pageNo, pageSize);
        PageResponse<AfterSaleDO> page = afterSaleService.getMerchantAfterSales(merchantId, pageNo, pageSize);
        return ApiResponse.success(page, RequestContext.getRequestId());
    }
}
