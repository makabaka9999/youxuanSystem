package com.youxuan.finance.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.finance.model.ReconciliationRecordDO;
import com.youxuan.finance.repository.ReconciliationRecordRepository;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 对账业务服务（ReconciliationService）。
 * <p>
 * 负责平台与支付渠道（微信、支付宝等）的对账处理，
 * 包括上传渠道账单、执行自动对账以及处理差异记录。
 * 对账是保证平台资金安全的关键环节。
 * </p>
 */
@Service
public class ReconciliationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReconciliationService.class);

    private final ReconciliationRecordRepository reconciliationRecordRepository;
    private final IdGenerator idGenerator;

    public ReconciliationService(ReconciliationRecordRepository reconciliationRecordRepository, IdGenerator idGenerator) {
        this.reconciliationRecordRepository = reconciliationRecordRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 上传渠道对账单并解析。
     * <p>
     * 接收支付渠道返回的对账单文件，解析为对账记录并落库。
     * 渠道对账单通常包含交易时间、交易单号、金额等字段。
     * </p>
     *
     * @param channel  支付渠道（WECHAT_PAY / ALIPAY）
     * @param billDate 对账日期
     */
    @Transactional
    public void uploadChannelBill(String channel, LocalDate billDate) {
        LOGGER.info("开始上传 {} 渠道对账单，日期：{}", channel, billDate);
        // 实际业务中：
        // 1. 解析渠道对账单文件（CSV/Excel）
        // 2. 逐行转换为 ReconciliationRecordDO
        // 3. 设置初始状态为 UNHANDLED
        // 4. 批量插入数据库
        LOGGER.info("{} 渠道对账单上传完成，日期：{}", channel, billDate);
    }

    /**
     * 执行自动对账。
     * <p>
     * 将平台交易记录与渠道对账单进行逐笔比对，
     * 标识出金额不一致、单边账等差异记录。
     * </p>
     *
     * @param channel  支付渠道
     * @param billDate 对账日期
     */
    @Transactional
    public void runReconciliation(String channel, LocalDate billDate) {
        LOGGER.info("开始执行 {} 渠道对账，日期：{}", channel, billDate);
        // 实际业务中：
        // 1. 查询渠道对账记录（reconciliation_records）
        // 2. 查询平台交易记录（payment_transactions）
        // 3. 按第三方交易号进行匹配
        // 4. 标记匹配成功的记录
        // 5. 对不匹配的记录设置 diff_type：
        //    - AMOUNT_MISMATCH：金额不一致
        //    - PLATFORM_ONLY：平台有，渠道无
        //    - CHANNEL_ONLY：渠道有，平台无
        LOGGER.info("{} 渠道对账执行完成，日期：{}", channel, billDate);
    }

    /**
     * 分页查询对账记录。
     *
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 分页对账记录
     */
    public PageResponse<ReconciliationRecordDO> listReconciliations(int pageNo, int pageSize) {
        List<ReconciliationRecordDO> list = reconciliationRecordRepository.findAll(pageNo, pageSize);
        int total = reconciliationRecordRepository.countAll();
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 按处理状态查询对账记录。
     *
     * @param handleStatus 处理状态
     * @param pageNo       页码
     * @param pageSize     每页条数
     * @return 分页对账记录
     */
    public PageResponse<ReconciliationRecordDO> listByHandleStatus(String handleStatus, int pageNo, int pageSize) {
        List<ReconciliationRecordDO> list = reconciliationRecordRepository.findByHandleStatus(handleStatus, pageNo, pageSize);
        int total = reconciliationRecordRepository.countByHandleStatus(handleStatus);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 处理对账差异。
     * <p>
     * 平台运营人员手动处理对账差异记录，
     * 将处理结果更新到记录中。
     * </p>
     *
     * @param id           对账记录 ID
     * @param handleResult 处理结果描述
     * @param resolved     是否已解决
     */
    @Transactional
    public void resolveDiff(Long id, String handleResult, boolean resolved) {
        ReconciliationRecordDO record = reconciliationRecordRepository.findByChannelAndDate(null, null)
                .stream().filter(r -> r.getId().equals(id))
                .findFirst().orElse(null);
        if (record == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "对账记录不存在");
        }
        String handleStatus = resolved ? "RESOLVED" : "IGNORED";
        reconciliationRecordRepository.updateStatus(id, handleStatus, handleResult);
        LOGGER.info("对账差异记录 {} 已处理，结果：{}", id, handleStatus);
    }
}
