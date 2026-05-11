package com.youxuan.admin.service;

import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 平台后台 - 商户管理服务（AdminMerchantService）。
 * <p>
 * 提供商户的查询、入驻审核、冻结/解冻等管理功能。
 * 实际业务中会调用 merchant-service 提供的接口或直接查询商户相关表。
 * </p>
 */
@Service
public class AdminMerchantService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminMerchantService.class);

    /**
     * 分页查询商户列表。
     *
     * @param keyword  搜索关键词（商户名称/联系人）
     * @param status   商户状态
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 商户列表（Map 表示）
     */
    public PageResponse<Map<String, Object>> listMerchants(String keyword, String status, int pageNo, int pageSize) {
        // 实际业务中查询 merchants 表
        LOGGER.info("查询商户列表，关键词：{}，状态：{}，页码：{}", keyword, status, pageNo);
        List<Map<String, Object>> list = new ArrayList<>();
        return new PageResponse<>(pageNo, pageSize, 0, list);
    }

    /**
     * 审核商户入驻申请。
     *
     * @param merchantId 商户 ID
     * @param approved   是否通过
     * @param reason     审核意见
     */
    public void auditMerchant(Long merchantId, boolean approved, String reason) {
        LOGGER.info("审核商户 {}，结果：{}，意见：{}", merchantId, approved, reason);
        // 实际业务中更新 merchants 表的 audit_status 字段
    }

    /**
     * 冻结商户账户。
     *
     * @param merchantId 商户 ID
     * @param reason     冻结原因
     */
    public void freezeMerchant(Long merchantId, String reason) {
        LOGGER.info("冻结商户 {}，原因：{}", merchantId, reason);
        // 实际业务中将商户状态更新为 FROZEN
    }

    /**
     * 解冻商户账户。
     *
     * @param merchantId 商户 ID
     * @param reason     解冻原因
     */
    public void unfreezeMerchant(Long merchantId, String reason) {
        LOGGER.info("解冻商户 {}，原因：{}", merchantId, reason);
        // 实际业务中将商户状态更新为 ACTIVE
    }
}
