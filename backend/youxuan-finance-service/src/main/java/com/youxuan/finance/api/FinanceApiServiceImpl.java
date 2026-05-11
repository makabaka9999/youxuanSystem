package com.youxuan.finance.api;

import com.youxuan.api.finance.FinanceApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 财务服务 Dubbo RPC 实现。
 */
@DubboService
public class FinanceApiServiceImpl implements FinanceApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FinanceApiServiceImpl.class);

    @Override
    public BigDecimal getAvailableWithdrawAmount(Long merchantId) {
        // TODO: 汇总已结算金额扣除已提现金额，待结算/账户模块完善后补充
        LOGGER.warn("getAvailableWithdrawAmount 被调用但尚未完整实现，merchantId={}", merchantId);
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasUnresolvedReconciliation(Long merchantId) {
        // TODO: 按商家查询未处理的对账差异记录，待对账模块完善后补充
        LOGGER.warn("hasUnresolvedReconciliation 被调用但尚未完整实现，merchantId={}", merchantId);
        return false;
    }
}
