package com.youxuan.merchant.api;

import com.youxuan.api.merchant.MerchantApiService;
import com.youxuan.merchant.model.MerchantApplicationDO;
import com.youxuan.merchant.model.StoreDO;
import com.youxuan.merchant.repository.MerchantRepository;
import com.youxuan.merchant.repository.StoreRepository;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 商家服务 Dubbo RPC 实现。
 */
@DubboService
public class MerchantApiServiceImpl implements MerchantApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantApiServiceImpl.class);

    private final MerchantRepository merchantRepository;
    private final StoreRepository storeRepository;

    public MerchantApiServiceImpl(MerchantRepository merchantRepository, StoreRepository storeRepository) {
        this.merchantRepository = merchantRepository;
        this.storeRepository = storeRepository;
    }

    @Override
    public boolean isMerchantEnabled(Long merchantId) {
        MerchantApplicationDO merchant = merchantRepository.findById(merchantId);
        if (merchant == null) {
            LOGGER.warn("商家不存在，merchantId={}", merchantId);
            return false;
        }
        return "APPROVED".equals(merchant.getAuditStatus()) && "ENABLED".equals(merchant.getStatus());
    }

    @Override
    public String getMerchantName(Long merchantId) {
        MerchantApplicationDO merchant = merchantRepository.findById(merchantId);
        return merchant != null ? merchant.getCompanyName() : null;
    }

    @Override
    public String getStoreName(Long storeId) {
        // 先按 merchantId 查找商家，再获取店铺
        // 实际场景中 storeId 与 merchantId 通常一一对应
        StoreDO store = storeRepository.findById(storeId);
        return store != null ? store.getStoreName() : null;
    }
}
