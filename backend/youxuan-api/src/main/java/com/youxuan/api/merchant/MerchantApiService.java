package com.youxuan.api.merchant;

/**
 * 商家服务 Dubbo API 接口
 *
 * <p>提供商家信息查询、状态校验等跨服务调用的能力，供商品、订单、管理等模块通过 Dubbo 远程调用。</p>
 */
public interface MerchantApiService {

    /**
     * 校验商家是否处于启用状态。
     *
     * @param merchantId 商家 ID
     * @return true 表示已启用，false 表示未启用或已禁用
     */
    boolean isMerchantEnabled(Long merchantId);

    /**
     * 获取商家名称。
     *
     * @param merchantId 商家 ID
     * @return 商家名称，若不存在则返回 null
     */
    String getMerchantName(Long merchantId);

    /**
     * 获取店铺名称。
     *
     * @param storeId 店铺 ID
     * @return 店铺名称，若不存在则返回 null
     */
    String getStoreName(Long storeId);
}
