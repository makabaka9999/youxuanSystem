package com.youxuan.finance.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.finance.model.WithdrawOrderDO;
import com.youxuan.finance.repository.WithdrawOrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提现业务服务（WithdrawService）。
 * <p>
 * 处理商户提现申请的提交、查询和平台审核流程。
 * 商户基于已审核通过的结算单发起提现，平台审核后
 * 通过支付渠道完成打款。
 * </p>
 */
@Service
public class WithdrawService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithdrawService.class);

    private final WithdrawOrderRepository withdrawOrderRepository;
    private final IdGenerator idGenerator;

    public WithdrawService(WithdrawOrderRepository withdrawOrderRepository, IdGenerator idGenerator) {
        this.withdrawOrderRepository = withdrawOrderRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 商户发起提现申请。
     * <p>
     * 基于已审核通过的结算单创建提现单，记录提现金额、
     * 收款账户等信息。实际业务中需校验结算单状态、
     * 商户可提现余额等前置条件。
     * </p>
     *
     * @param merchantId   商户 ID
     * @param settlementId 结算单 ID
     * @param accountId    收款账户 ID
     * @param amount       提现金额
     * @param idempotentKey 幂等键
     */
    @Transactional
    public void applyWithdraw(Long merchantId, Long settlementId, Long accountId,
                              java.math.BigDecimal amount, String idempotentKey) {
        LOGGER.info("商户 {} 发起提现申请，结算单：{}，金额：{}", merchantId, settlementId, amount);
        // 实际业务中需校验：
        // 1. 结算单状态是否为 APPROVED
        // 2. 提现金额是否超过可提现金额
        // 3. 收款账户是否属于该商户
        // 4. 幂等键校验避免重复提交

        WithdrawOrderDO withdraw = new WithdrawOrderDO();
        withdraw.setId(idGenerator.nextId());
        withdraw.setWithdrawNo("WD" + System.currentTimeMillis());
        withdraw.setMerchantId(merchantId);
        withdraw.setSettlementId(settlementId);
        withdraw.setAccountId(accountId);
        withdraw.setAmount(amount);
        withdraw.setStatus("PENDING_AUDIT");
        withdraw.setIdempotentKey(idempotentKey);
        withdrawOrderRepository.insert(withdraw);
        LOGGER.info("提现申请已提交，提现单号：{}", withdraw.getWithdrawNo());
    }

    /**
     * 分页查询商户提现单列表（商户端）。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页提现单结果
     */
    public PageResponse<WithdrawOrderDO> listWithdraws(Long merchantId, int pageNo, int pageSize) {
        List<WithdrawOrderDO> list = withdrawOrderRepository.findByMerchantId(merchantId, pageNo, pageSize);
        int total = withdrawOrderRepository.countByMerchantId(merchantId);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 分页查询所有提现单（平台后台使用）。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页提现单结果
     */
    public PageResponse<WithdrawOrderDO> listAllWithdraws(int pageNo, int pageSize) {
        List<WithdrawOrderDO> list = withdrawOrderRepository.findAll(pageNo, pageSize);
        int total = withdrawOrderRepository.countAll();
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 按状态查询提现单列表（平台后台使用）。
     *
     * @param status   状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页提现单结果
     */
    public PageResponse<WithdrawOrderDO> listWithdrawsByStatus(String status, int pageNo, int pageSize) {
        List<WithdrawOrderDO> list = withdrawOrderRepository.findByStatus(status, pageNo, pageSize);
        int total = withdrawOrderRepository.countByStatus(status);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 平台审核提现单。
     *
     * @param id          提现单 ID
     * @param approved    是否通过
     * @param auditReason 审核意见
     */
    @Transactional
    public void auditWithdraw(Long id, boolean approved, String auditReason) {
        WithdrawOrderDO withdraw = withdrawOrderRepository.findById(id);
        if (withdraw == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "提现单不存在");
        }
        if (!"PENDING_AUDIT".equals(withdraw.getStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "提现单状态不是待审核");
        }

        Long auditUserId = JwtRequestContext.get() != null ? JwtRequestContext.get().getUserId() : null;
        String targetStatus = approved ? "APPROVED" : "REJECTED";
        int rows = withdrawOrderRepository.updateStatus(id, targetStatus, auditUserId, auditReason, withdraw.getVersion());
        if (rows == 0) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "数据已被修改，请刷新后重试");
        }
        LOGGER.info("提现单 {} 审核完成，结果：{}", id, targetStatus);
    }

    /**
     * 更新提现支付结果。
     * <p>
     * 调用支付渠道完成打款后，更新提现单的支付状态。
     * </p>
     *
     * @param id         提现单 ID
     * @param success    是否支付成功
     * @param failReason 失败原因（可选）
     */
    @Transactional
    public void updatePayStatus(Long id, boolean success, String failReason) {
        WithdrawOrderDO withdraw = withdrawOrderRepository.findById(id);
        if (withdraw == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "提现单不存在");
        }

        String targetStatus = success ? "SUCCESS" : "FAILED";
        LocalDateTime paidAt = success ? LocalDateTime.now() : null;
        int rows = withdrawOrderRepository.updatePayStatus(id, targetStatus, failReason, paidAt, withdraw.getVersion());
        if (rows == 0) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "数据已被修改，请刷新后重试");
        }
        LOGGER.info("提现单 {} 支付状态更新为：{}", id, targetStatus);
    }
}
