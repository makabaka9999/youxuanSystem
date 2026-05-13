package com.youxuan.api.finance;

import java.math.BigDecimal;

/**
 * 财务服务 Dubbo API 接口
 *
 * <p>提供账户余额查询、对账状态校验等跨服务调用的能力，供商家、管理等模块通过 Dubbo 远程调用。</p>
 */
public interface FinanceApiService {

    /**
     * 获取商家当前可提现金额。
     *
     * @param merchantId 商家 ID
     * @return 可提现金额
     */
    BigDecimal getAvailableWithdrawAmount(Long merchantId);

    /**
     * 校验商家是否存在未处理的对账差异。
     *
     * @param merchantId 商家 ID
     * @return true 表示存在未处理差异，false 表示无差异
     */
    boolean hasUnresolvedReconciliation(Long merchantId);
}
