package com.youxuan.merchant.service;

import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.merchant.dto.StoreUpdateRequest;
import com.youxuan.merchant.model.StoreDO;
import com.youxuan.merchant.repository.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 店铺管理服务。
 */
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final IdGenerator idGenerator;

    public StoreService(StoreRepository storeRepository, IdGenerator idGenerator) {
        this.storeRepository = storeRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 根据商家 ID 查询店铺信息。
     *
     * @param merchantId 商家 ID
     * @return 店铺对象
     */
    public StoreDO getStore(Long merchantId) {
        StoreDO store = storeRepository.findByMerchantId(merchantId);
        if (store == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "店铺信息不存在，请先完成入驻");
        }
        return store;
    }

    /**
     * 更新店铺信息。
     *
     * @param merchantId 商家 ID
     * @param request    店铺更新请求
     */
    @Transactional
    public void updateStore(Long merchantId, StoreUpdateRequest request) {
        StoreDO store = storeRepository.findByMerchantId(merchantId);
        if (store == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "店铺信息不存在，请先完成入驻");
        }
        if (request.getStoreName() != null) {
            store.setStoreName(request.getStoreName());
        }
        if (request.getLogoUrl() != null) {
            store.setLogoUrl(request.getLogoUrl());
        }
        if (request.getContactMobile() != null) {
            store.setContactMobile(request.getContactMobile());
        }
        if (request.getCategoryId() != null) {
            store.setCategoryId(request.getCategoryId());
        }
        if (request.getRemark() != null) {
            store.setRemark(request.getRemark());
        }
        storeRepository.update(store);
    }
}
