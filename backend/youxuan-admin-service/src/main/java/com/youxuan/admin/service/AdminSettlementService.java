package com.youxuan.admin.service;

import com.youxuan.common.api.ApiResponse;
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
 * 平台后台 - 结算管理服务（AdminSettlementService）。
 * <p>
 * 提供结算单的查询和审核功能，平台管理员可查看所有商户的
 * 结算单并对处于待审核状态的结算单进行审核操作。
 * 实际业务中会调用 finance-service 的接口。
 * </p>
 */
@Service
public class AdminSettlementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSettlementService.class);

    /**
     * 分页查询结算单列表（平台后台视角）。
     *
     * @param merchantId 商户 ID（可选）
     * @param status     状态（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页结算单列表
     */
    public PageResponse<Map<String, Object>> listSettlements(Long merchantId, String status, int pageNo, int pageSize) {
        LOGGER.info("平台查询结算单，商户：{}，状态：{}", merchantId, status);
        // 实际业务中调用 SettlementOrderRepository 或 finance-service API
        List<Map<String, Object>> list = new ArrayList<>();
        return new PageResponse<>(pageNo, pageSize, 0, list);
    }

    /**
     * 平台审核结算单。
     *
     * @param id          结算单 ID
     * @param approved    是否通过
     * @param auditReason 审核意见
     */
    public void auditSettlement(Long id, boolean approved, String auditReason) {
        LOGGER.info("平台审核结算单 {}，结果：{}，意见：{}", id, approved, auditReason);
        // 实际业务中通过 Feign 调用 finance-service 的结算审核接口
    }
}
