package com.youxuan.finance.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.finance.model.SettlementOrderDO;
import com.youxuan.finance.repository.SettlementOrderRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 结算业务服务（SettlementService）。
 * <p>
 * 负责结算单的生成、查询以及平台审核操作。
 * 结算单基于商户已确认的账单按周期汇总生成，
 * 是商户提现的前置条件。
 * </p>
 */
@Service
public class SettlementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettlementService.class);

    private final SettlementOrderRepository settlementOrderRepository;
    private final IdGenerator idGenerator;

    public SettlementService(SettlementOrderRepository settlementOrderRepository, IdGenerator idGenerator) {
        this.settlementOrderRepository = settlementOrderRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 为商户生成指定周期的结算单。
     * <p>
     * 汇总该周期内商户已确认的账单数据，生成一条结算单记录。
     * 实际业务中需查询账单表中对应商户在结算周期内的已确认账单。
     * </p>
     *
     * @param merchantId 商户 ID
     * @param startDate  结算周期起始日期
     * @param endDate    结算周期结束日期
     */
    @Transactional
    public void generateSettlement(Long merchantId, LocalDate startDate, LocalDate endDate) {
        LOGGER.info("开始生成商户 {} 结算单，周期：{} ~ {}", merchantId, startDate, endDate);
        // 实际业务中需：
        // 1. 查询 merchant_bills 中该商户在 startDate~endDate 间状态为 CONFIRMED 的账单
        // 2. 汇总 order_amount、refund_amount、commission_amount
        // 3. 计算应付金额
        // 4. 生成 settlement_orders 记录
        // 5. 生成 settlement_order_items 明细

        SettlementOrderDO settlement = new SettlementOrderDO();
        settlement.setId(idGenerator.nextId());
        settlement.setSettlementNo("SET" + System.currentTimeMillis());
        settlement.setMerchantId(merchantId);
        settlement.setStartDate(startDate);
        settlement.setEndDate(endDate);
        settlement.setStatus("PENDING_AUDIT");
        settlementOrderRepository.insert(settlement);
        LOGGER.info("商户 {} 结算单已生成，结算单号：{}", merchantId, settlement.getSettlementNo());
    }

    /**
     * 分页查询商户结算单列表（商户端）。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页结算单结果
     */
    public PageResponse<SettlementOrderDO> listSettlements(Long merchantId, int pageNo, int pageSize) {
        List<SettlementOrderDO> list = settlementOrderRepository.findByMerchantId(merchantId, pageNo, pageSize);
        int total = settlementOrderRepository.countByMerchantId(merchantId);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 分页查询所有结算单（平台后台使用）。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结算单结果
     */
    public PageResponse<SettlementOrderDO> listAllSettlements(int pageNo, int pageSize) {
        List<SettlementOrderDO> list = settlementOrderRepository.findAll(pageNo, pageSize);
        int total = settlementOrderRepository.countAll();
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 按状态查询结算单列表（平台后台使用）。
     *
     * @param status   状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页结算单结果
     */
    public PageResponse<SettlementOrderDO> listSettlementsByStatus(String status, int pageNo, int pageSize) {
        List<SettlementOrderDO> list = settlementOrderRepository.findByStatus(status, pageNo, pageSize);
        int total = settlementOrderRepository.countByStatus(status);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 根据 ID 查询结算单详情。
     *
     * @param id 结算单 ID
     * @return 结算单数据对象
     */
    public SettlementOrderDO getSettlementById(Long id) {
        return settlementOrderRepository.findById(id);
    }

    /**
     * 审核结算单。
     * <p>
     * 平台管理员对结算单进行审核，审核通过后商户可发起提现，
     * 审核驳回则退回商户重新确认。
     * </p>
     *
     * @param id         结算单 ID
     * @param approved   是否通过
     * @param auditReason 审核意见
     */
    @Transactional
    public void auditSettlement(Long id, boolean approved, String auditReason) {
        SettlementOrderDO settlement = settlementOrderRepository.findById(id);
        if (settlement == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "结算单不存在");
        }
        if (!"PENDING_AUDIT".equals(settlement.getStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "结算单状态不是待审核");
        }

        Long auditUserId = JwtRequestContext.get() != null ? JwtRequestContext.get().getUserId() : null;
        String targetStatus = approved ? "APPROVED" : "REJECTED";
        int rows = settlementOrderRepository.updateStatus(id, targetStatus, auditUserId, auditReason, settlement.getVersion());
        if (rows == 0) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "数据已被修改，请刷新后重试");
        }
        LOGGER.info("结算单 {} 审核完成，结果：{}，意见：{}", id, targetStatus, auditReason);
    }
}
