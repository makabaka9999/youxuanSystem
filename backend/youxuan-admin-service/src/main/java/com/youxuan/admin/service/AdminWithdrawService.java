package com.youxuan.admin.service;

import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 平台后台 - 提现管理服务（AdminWithdrawService）。
 * <p>
 * 提供提现单的查询、审核和支付状态更新功能。
 * 平台管理员审核商户的提现申请，审核通过后由财务人员
 * 通过支付渠道完成打款操作。
 * </p>
 */
@Service
public class AdminWithdrawService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminWithdrawService.class);

    /**
     * 分页查询提现单列表（平台后台视角）。
     *
     * @param merchantId 商户 ID（可选）
     * @param status     状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页提现单列表
     */
    public PageResponse<Map<String, Object>> listWithdraws(Long merchantId, String status, int pageNo, int pageSize) {
        LOGGER.info("平台查询提现单，商户：{}，状态：{}", merchantId, status);
        // 实际业务中调用 WithdrawOrderRepository 或 finance-service API
        List<Map<String, Object>> list = new ArrayList<>();
        return new PageResponse<>(pageNo, pageSize, 0, list);
    }

    /**
     * 平台审核提现单。
     *
     * @param id          提现单 ID
     * @param approved    是否通过
     * @param auditReason 审核意见
     */
    public void auditWithdraw(Long id, boolean approved, String auditReason) {
        LOGGER.info("平台审核提现单 {}，结果：{}，意见：{}", id, approved, auditReason);
        // 实际业务中通过 Feign 调用 finance-service 的提现审核接口
    }

    /**
     * 更新提现单支付状态。
     * <p>
     * 财务人员完成打款后，更新提现单的支付结果。
     * </p>
     *
     * @param id           提现单 ID
     * @param paySuccess   是否支付成功
     * @param failReason   失败原因（可选）
     */
    public void updatePayStatus(Long id, boolean paySuccess, String failReason) {
        LOGGER.info("更新提现单 {} 支付状态，成功：{}，原因：{}", id, paySuccess, failReason);
        // 实际业务中通过 Feign 调用 finance-service 的支付状态更新接口
    }
}
