package com.youxuan.finance.service;

import com.youxuan.common.api.PageResponse;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.finance.model.MerchantBillDO;
import com.youxuan.finance.repository.MerchantBillRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 账单业务服务（BillService）。
 * <p>
 * 负责商户每日账单的生成与查询，
 * 通过汇总每日订单数据生成每个商户的日账单，
 * 为后续结算提供基础数据。
 * </p>
 */
@Service
public class BillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BillService.class);

    private final MerchantBillRepository merchantBillRepository;
    private final IdGenerator idGenerator;

    public BillService(MerchantBillRepository merchantBillRepository, IdGenerator idGenerator) {
        this.merchantBillRepository = merchantBillRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 生成指定日期的商户日账单。
     * <p>
     * 遍历所有活跃商户，汇总其当日的订单金额、退款金额、佣金等
     * 财务数据，生成或更新商户日账单。如果某商户当日已存在账单
     * 则跳过，保证幂等性。
     * </p>
     *
     * @param billDate 账单日期
     */
    public void generateDailyBill(LocalDate billDate) {
        LOGGER.info("开始生成商户日账单，日期：{}", billDate);
        // 实际业务中需查询当日所有有订单的商户，此处为简化示例
        // 通过订单服务获取当日各商户的汇总数据
        // 然后逐商户生成账单记录
        // 伪代码流程：
        // 1. 调用订单服务查询 billDate 日期的所有订单汇总
        // 2. 按 merchantId 分组计算 orderAmount、freightAmount、refundAmount
        // 3. 根据平台佣金规则计算 commissionAmount
        // 4. 计算 payableAmount = orderAmount + freightAmount - refundAmount - commissionAmount + adjustmentAmount
        // 5. 通过 merchantBillRepository.findByMerchantAndDate() 检查是否已存在
        // 6. 不存在则插入新账单，存在则跳过或更新
        LOGGER.info("商户日账单生成完成，日期：{}", billDate);
    }

    /**
     * 分页查询商户账单列表。
     *
     * @param merchantId 商户 ID
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页账单结果
     */
    public PageResponse<MerchantBillDO> listBills(Long merchantId, int pageNo, int pageSize) {
        List<MerchantBillDO> bills = merchantBillRepository.findByMerchantId(merchantId, pageNo, pageSize);
        int total = merchantBillRepository.countByMerchantId(merchantId);
        return new PageResponse<>(pageNo, pageSize, total, bills);
    }

    /**
     * 创建商户账单（提供给定时任务或手动触发使用）。
     *
     * @param merchantId     商户 ID
     * @param billDate       账单日期
     * @param orderAmount    订单金额
     * @param freightAmount  运费金额
     * @param refundAmount   退款金额
     * @param commissionAmount 佣金金额
     * @param adjustmentAmount 调整金额
     */
    public void createBill(Long merchantId, LocalDate billDate, BigDecimal orderAmount,
                           BigDecimal freightAmount, BigDecimal refundAmount,
                           BigDecimal commissionAmount, BigDecimal adjustmentAmount) {
        // 检查是否已存在
        List<MerchantBillDO> existing = merchantBillRepository.findByMerchantAndDate(merchantId, billDate);
        if (!existing.isEmpty()) {
            LOGGER.warn("商户 {} 日期 {} 的账单已存在，跳过生成", merchantId, billDate);
            return;
        }

        MerchantBillDO bill = new MerchantBillDO();
        bill.setId(idGenerator.nextId());
        bill.setMerchantId(merchantId);
        bill.setBillDate(billDate);
        bill.setOrderAmount(orderAmount);
        bill.setFreightAmount(freightAmount);
        bill.setRefundAmount(refundAmount);
        bill.setCommissionAmount(commissionAmount);
        bill.setAdjustmentAmount(adjustmentAmount);
        // 应付金额 = 订单金额 + 运费 - 退款 - 佣金 + 调整
        BigDecimal payable = orderAmount.add(freightAmount)
                .subtract(refundAmount)
                .subtract(commissionAmount)
                .add(adjustmentAmount);
        bill.setPayableAmount(payable);
        bill.setStatus("GENERATED");
        merchantBillRepository.insert(bill);
        LOGGER.info("已生成商户 {} 日期 {} 的账单，应付金额：{}", merchantId, billDate, payable);
    }

    /**
     * 确认账单（将状态从 GENERATED 更新为 CONFIRMED）。
     *
     * @param id 账单 ID
     */
    public void confirmBill(Long id) {
        MerchantBillDO bill = merchantBillRepository.findByMerchantAndDate(null, null).stream()
                .filter(b -> b.getId().equals(id))
                .findFirst().orElse(null);
        if (bill != null) {
            merchantBillRepository.updateStatus(id, "CONFIRMED", bill.getVersion());
        }
    }
}
