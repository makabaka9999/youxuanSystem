package com.youxuan.admin.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 平台后台 - 商品管理服务（AdminProductService）。
 * <p>
 * 提供待审核商品的查询和审核操作，平台管理员可审核
 * 商户提交的新商品或修改后的商品信息。
 * </p>
 */
@Service
public class AdminProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminProductService.class);

    /**
     * 分页查询待审核商品列表。
     *
     * @param keyword  搜索关键词
     * @param merchantId 商户 ID（可选）
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 待审核商品列表
     */
    public PageResponse<Map<String, Object>> listPendingAuditProducts(String keyword, Long merchantId, int pageNo, int pageSize) {
        LOGGER.info("查询待审核商品，关键词：{}，商户：{}", keyword, merchantId);
        // 实际业务中查询 products 表 WHERE audit_status = 'PENDING'
        List<Map<String, Object>> list = new ArrayList<>();
        return new PageResponse<>(pageNo, pageSize, 0, list);
    }

    /**
     * 审核商品。
     * <p>
     * 对商户提交的商品进行审核，通过后商品变为上架状态，
     * 驳回则退回商户修改。
     * </p>
     *
     * @param productId 商品 ID
     * @param approved  是否通过
     * @param reason    审核意见（驳回时必填）
     */
    public void auditProduct(Long productId, boolean approved, String reason) {
        LOGGER.info("审核商品 {}，结果：{}，意见：{}", productId, approved, reason);
        if (!approved && (reason == null || reason.isEmpty())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "驳回时必须填写审核意见");
        }
        // 实际业务中更新 products 表的 audit_status 和 reject_reason
    }
}
