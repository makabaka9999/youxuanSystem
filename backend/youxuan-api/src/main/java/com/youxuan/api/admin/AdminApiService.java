package com.youxuan.api.admin;

/**
 * 平台管理服务 Dubbo API 接口
 *
 * <p>提供商家冻结校验、平台配置查询等跨服务调用的能力，供其他模块通过 Dubbo 远程调用。</p>
 */
public interface AdminApiService {

    /**
     * 校验指定商家是否已被平台冻结。
     *
     * @param merchantId 商家 ID
     * @return true 表示已被冻结，false 表示未被冻结
     */
    boolean isMerchantFrozen(Long merchantId);

    /**
     * 获取平台配置项的值。
     *
     * @param configKey 配置键
     * @return 配置值，若不存在则返回 null
     */
    String getConfigValue(String configKey);
}
