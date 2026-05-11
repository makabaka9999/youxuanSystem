package com.youxuan.merchant.service;

import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.merchant.dto.CreateStaffRequest;
import com.youxuan.merchant.model.MerchantStaffDO;
import com.youxuan.merchant.repository.MerchantStaffRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家员工管理服务。
 */
@Service
public class MerchantStaffService {

    private final MerchantStaffRepository staffRepository;
    private final IdGenerator idGenerator;

    public MerchantStaffService(MerchantStaffRepository staffRepository, IdGenerator idGenerator) {
        this.staffRepository = staffRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 查询商家下所有员工。
     *
     * @param merchantId 商家 ID
     * @return 员工列表
     */
    public List<MerchantStaffDO> listStaff(Long merchantId) {
        return staffRepository.findByMerchantId(merchantId);
    }

    /**
     * 创建员工。
     *
     * @param merchantId 商家 ID
     * @param request    创建员工请求
     * @return 员工对象
     */
    @Transactional
    public MerchantStaffDO createStaff(Long merchantId, CreateStaffRequest request) {
        // 检查该用户是否已经是本商家员工
        MerchantStaffDO existing = staffRepository.findByMerchantAndUser(merchantId, request.getUserId());
        if (existing != null) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "该用户已是本商家员工");
        }

        MerchantStaffDO staff = new MerchantStaffDO();
        staff.setId(idGenerator.nextId());
        staff.setMerchantId(merchantId);
        staff.setUserId(request.getUserId());
        staff.setStaffName(request.getStaffName());
        staff.setRoleType(request.getRoleType());
        staff.setMenuPermissions(request.getMenuPermissions());
        staff.setStatus("ENABLED");
        staff.setRemark(request.getRemark());
        staffRepository.insert(staff);
        return staff;
    }

    /**
     * 切换员工启用/禁用状态。
     *
     * @param merchantId 商家 ID
     * @param staffId    员工 ID
     */
    @Transactional
    public void toggleStatus(Long merchantId, Long staffId) {
        MerchantStaffDO staff = staffRepository.findByMerchantAndUser(merchantId, null);
        // 直接按 ID 查找校验归属
        List<MerchantStaffDO> allStaff = staffRepository.findByMerchantId(merchantId);
        MerchantStaffDO target = allStaff.stream()
                .filter(s -> s.getId().equals(staffId))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.RESOURCE_NOT_FOUND, "员工不存在"));

        String newStatus = "ENABLED".equals(target.getStatus()) ? "DISABLED" : "ENABLED";
        staffRepository.updateStatus(staffId, newStatus);
    }
}
