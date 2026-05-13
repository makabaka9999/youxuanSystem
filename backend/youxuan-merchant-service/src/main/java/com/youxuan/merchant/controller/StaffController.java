package com.youxuan.merchant.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.security.JwtRequestContext;
import com.youxuan.common.web.RequestContext;
import com.youxuan.merchant.dto.CreateStaffRequest;
import com.youxuan.merchant.model.MerchantStaffDO;
import com.youxuan.merchant.service.MerchantStaffService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家员工管理接口。
 */
@RestController
@RequestMapping("/api/v1/merchant/staffs")
public class StaffController {

    private final MerchantStaffService staffService;

    public StaffController(MerchantStaffService staffService) {
        this.staffService = staffService;
    }

    /**
     * 根据手机号查找用户（添加员工时使用）。
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @GetMapping("/lookup")
    public ApiResponse<Map<String, Object>> lookupUser(@RequestParam String mobile) {
        Map<String, Object> user = staffService.lookupByMobile(mobile);
        if (user == null) {
            return ApiResponse.fail(ErrorCode.RESOURCE_NOT_FOUND.getCode(), "该手机号未注册", RequestContext.getRequestId());
        }
        return ApiResponse.success(user, RequestContext.getRequestId());
    }

    /**
     * 查询当前商家下的所有员工。
     *
     * @return 员工列表
     */
    @GetMapping
    public ApiResponse<List<MerchantStaffDO>> listStaff() {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        List<MerchantStaffDO> staffList = staffService.listStaff(merchantId);
        return ApiResponse.success(staffList, RequestContext.getRequestId());
    }

    /**
     * 创建员工。
     *
     * @param request 创建员工信息
     * @return 创建成功的员工
     */
    @PostMapping
    public ApiResponse<MerchantStaffDO> createStaff(@RequestBody CreateStaffRequest request) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        MerchantStaffDO staff = staffService.createStaff(merchantId, request);
        return ApiResponse.success(staff, RequestContext.getRequestId());
    }

    /**
     * 切换员工启用/禁用状态。
     *
     * @param staffId 员工 ID
     * @return 操作结果
     */
    @PutMapping("/{staffId}/status")
    public ApiResponse<Void> toggleStaff(@PathVariable Long staffId) {
        Long merchantId = JwtRequestContext.get().getMerchantId();
        staffService.toggleStatus(merchantId, staffId);
        return ApiResponse.success(null, RequestContext.getRequestId());
    }
}
