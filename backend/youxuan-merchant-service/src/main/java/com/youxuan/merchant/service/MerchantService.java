package com.youxuan.merchant.service;

import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.merchant.dto.DashboardDTO;
import com.youxuan.merchant.dto.MerchantApplicationRequest;
import com.youxuan.merchant.model.MerchantApplicationDO;
import com.youxuan.merchant.model.StoreDO;
import com.youxuan.merchant.repository.MerchantRepository;
import com.youxuan.merchant.repository.ProductRepository;
import com.youxuan.merchant.repository.StoreRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家入驻与基础信息服务。
 */
@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final IdGenerator idGenerator;

    public MerchantService(MerchantRepository merchantRepository,
                           StoreRepository storeRepository,
                           ProductRepository productRepository,
                           IdGenerator idGenerator) {
        this.merchantRepository = merchantRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 提交入驻申请。
     *
     * @param userId    申请用户 ID
     * @param request   入驻申请请求
     * @return 入驻申请对象
     */
    @Transactional
    public MerchantApplicationDO apply(Long userId, MerchantApplicationRequest request) {
        // 校验该用户是否已存在有效的入驻申请
        MerchantApplicationDO existing = merchantRepository.findByOwnerUserId(userId);
        if (existing != null && "PENDING".equals(existing.getAuditStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "您已提交过入驻申请，请等待审核");
        }

        MerchantApplicationDO merchant = new MerchantApplicationDO();
        merchant.setId(idGenerator.nextId());
        merchant.setOwnerUserId(userId);
        merchant.setCompanyName(request.getCompanyName());
        merchant.setLicenseNo(request.getLicenseNo());
        merchant.setContactName(request.getContactName());
        merchant.setContactMobile(request.getContactMobile());
        merchant.setAuditStatus("PENDING");
        merchant.setStatus("ENABLED");
        merchant.setRemark(request.getRemark());
        merchantRepository.insert(merchant);
        return merchant;
    }

    /**
     * 根据 ID 查询入驻申请。
     *
     * @param id 入驻申请 ID
     * @return 入驻申请对象
     */
    public MerchantApplicationDO getMerchant(Long id) {
        MerchantApplicationDO merchant = merchantRepository.findById(id);
        if (merchant == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "商家入驻申请不存在");
        }
        return merchant;
    }

    /**
     * 根据用户 ID 查询该用户的入驻申请。
     *
     * @param userId 用户 ID
     * @return 入驻申请对象，不存在返回 null
     */
    public MerchantApplicationDO getMerchantByOwner(Long userId) {
        return merchantRepository.findByOwnerUserId(userId);
    }

    /**
     * 查询商家工作台仪表盘数据。
     *
     * @param merchantId 商家 ID
     * @return 仪表盘数据
     */
    public DashboardDTO getDashboard(Long merchantId) {
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setTotalProductCount((int) productRepository.countByMerchantId(merchantId, null, null, null, null));
        dashboard.setOnSaleProductCount((int) productRepository.countByMerchantIdAndSaleStatus(merchantId, "ON_SALE"));
        dashboard.setPendingAuditProductCount((int) productRepository.countByMerchantIdAndAuditStatus(merchantId, "PENDING"));
        return dashboard;
    }
}
