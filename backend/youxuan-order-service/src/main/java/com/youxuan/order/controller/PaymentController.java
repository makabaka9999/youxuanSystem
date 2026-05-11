package com.youxuan.order.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.security.JwtClaims;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.order.dto.CreatePaymentRequest;
import com.youxuan.order.model.PaymentOrderDO;
import com.youxuan.order.service.PaymentService;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付控制器
 * <p>
 * 提供支付单创建和支付回调处理接口。
 * P0 阶段支付为模拟实现，不对接真实第三方支付渠道。
 * 用户创建支付单后，系统可通过模拟回调的方式来推进订单支付状态。
 * </p>
 */
@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付单
     * <p>
     * 用户选择订单并发起支付时调用，创建一个支付记录。
     * 一个订单只能有一笔有效的支付单。
     * </p>
     *
     * @param request 创建支付请求体
     * @return 支付单对象
     */
    @PostMapping("/payments")
    public ApiResponse<PaymentOrderDO> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        JwtClaims claims = JwtRequestContext.get();
        Long userId = claims.getUserId();
        LOGGER.info("创建支付单. userId={}, orderNo={}, channel={}", userId, request.getOrderNo(), request.getChannel());
        PaymentOrderDO payment = paymentService.createPayment(userId, request.getOrderNo(), request.getChannel());
        return ApiResponse.success(payment, RequestContext.getRequestId());
    }

    /**
     * 支付回调处理
     * <p>
     * P0 阶段模拟第三方支付渠道的回调接口。
     * 调用此接口模拟支付结果通知，推动订单状态流转。
     * 请求体中的 "paymentNo" 字段标识支付单，"result" 字段传 "SUCCESS" 或 "FAILED"。
     * </p>
     *
     * @param channel 支付渠道（如 "MOCK"）
     * @param body    回调请求体（含 paymentNo、result 等）
     * @return 操作结果
     */
    @PostMapping("/payment-callbacks/{channel}")
    public ApiResponse<Void> paymentCallback(@PathVariable String channel, @RequestBody Map<String, String> body) {
        String paymentNo = body.get("paymentNo");
        LOGGER.info("收到支付回调. channel={}, paymentNo={}", channel, paymentNo);
        paymentService.handlePaymentCallback(channel, paymentNo);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
