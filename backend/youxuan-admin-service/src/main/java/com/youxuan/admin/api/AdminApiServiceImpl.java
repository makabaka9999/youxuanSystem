package com.youxuan.admin.api;

import com.youxuan.api.admin.AdminApiService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 平台管理服务 Dubbo RPC 实现。
 */
@DubboService
public class AdminApiServiceImpl implements AdminApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminApiServiceImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public AdminApiServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isMerchantFrozen(Long merchantId) {
        // 直接查询 merchant_applications 表判断商家是否被冻结
        String sql = "SELECT status FROM merchant_applications WHERE id = ? AND deleted_at IS NULL";
        try {
            String status = jdbcTemplate.queryForObject(sql, String.class, merchantId);
            return "DISABLED".equals(status);
        } catch (Exception e) {
            LOGGER.warn("查询商家状态失败，merchantId={}", merchantId, e);
            return false;
        }
    }

    @Override
    public String getConfigValue(String configKey) {
        // TODO: 待平台配置表实现后补充查询逻辑
        LOGGER.warn("getConfigValue 被调用但配置表尚未实现，configKey={}", configKey);
        return null;
    }
}
