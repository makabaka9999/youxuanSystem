package com.youxuan.merchant.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.merchant.dto.CreateStaffRequest;
import com.youxuan.merchant.model.MerchantStaffDO;
import com.youxuan.merchant.repository.MerchantStaffRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家员工管理服务。
 */
@Service
public class MerchantStaffService {

    /** MERCHANT_STAFF 角色在 roles 表中的 ID（来自 V2 种子数据） */
    private static final Long MERCHANT_STAFF_ROLE_ID = 2002L;

    private final MerchantStaffRepository staffRepository;
    private final IdGenerator idGenerator;
    private final JdbcTemplate jdbcTemplate;

    public MerchantStaffService(MerchantStaffRepository staffRepository,
                                IdGenerator idGenerator,
                                JdbcTemplate jdbcTemplate) {
        this.staffRepository = staffRepository;
        this.idGenerator = idGenerator;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 查询商家下所有员工。
     *
     * @param merchantId 商家 ID
     * @return 员工列表
     */
    public List<MerchantStaffDO> listStaff(Long merchantId, String keyword) {
        return staffRepository.findByMerchantId(merchantId, keyword);
    }

    /**
     * 根据手机号查找用户信息。
     *
     * @param mobile 手机号
     * @return 用户信息映射（id, mobile, nickname），不存在返回 null
     */
    public Map<String, Object> lookupByMobile(String mobile) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, mobile, nickname FROM users WHERE mobile = ? AND deleted_at IS NULL",
                mobile);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> user = new HashMap<>(3);
        user.put("id", rows.get(0).get("id"));
        user.put("mobile", rows.get(0).get("mobile"));
        user.put("nickname", rows.get(0).get("nickname"));
        return user;
    }

    /**
     * 创建员工（含 RBAC 角色关联）。
     *
     * @param merchantId 商家 ID
     * @param request    创建员工请求
     * @return 员工对象
     */
    @Transactional
    public MerchantStaffDO createStaff(Long merchantId, CreateStaffRequest request) {
        // 1. 通过手机号查找用户
        Long userId = staffRepository.findUserIdByMobile(request.getMobile());
        if (userId == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "该手机号未注册");
        }

        // 2. 检查该用户是否已经是本商家员工
        MerchantStaffDO existing = staffRepository.findByMerchantAndUser(merchantId, userId);
        if (existing != null) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "该用户已是本商家员工");
        }

        // 3. 创建员工记录
        MerchantStaffDO staff = new MerchantStaffDO();
        staff.setId(idGenerator.nextId());
        staff.setMerchantId(merchantId);
        staff.setUserId(userId);
        staff.setStaffName(request.getStaffName());
        staff.setRoleType(request.getRoleType());
        staff.setMenuPermissions(request.getMenuPermissions());
        staff.setStatus("ENABLED");
        staff.setRemark(request.getRemark());
        staffRepository.insert(staff);

        // 4. 创建 RBAC 角色关联（user_roles）
        jdbcTemplate.update(
                "INSERT INTO user_roles (id, principal_type, principal_id, role_id, created_at, updated_at, version) " +
                "VALUES (?, 'MERCHANT_STAFF', ?, ?, NOW(), NOW(), 0)",
                idGenerator.nextId(), staff.getId(), MERCHANT_STAFF_ROLE_ID);

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
        List<MerchantStaffDO> allStaff = staffRepository.findByMerchantId(merchantId, null);
        MerchantStaffDO target = allStaff.stream()
                .filter(s -> s.getId().equals(staffId))
                .findFirst()
                .orElseThrow(() -> new BizException(ErrorCode.RESOURCE_NOT_FOUND, "员工不存在"));

        String newStatus = "ENABLED".equals(target.getStatus()) ? "DISABLED" : "ENABLED";
        staffRepository.updateStatus(staffId, newStatus);
    }
}
