package com.youxuan.gateway.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.constant.ApiConstants;
import com.youxuan.common.web.RequestContext;
import com.youxuan.gateway.dto.ServiceDirectoryDTO;
import com.youxuan.gateway.service.ServiceDirectoryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务目录控制器
 * <p>
 * 提供平台所有微服务的目录列表，方便前端和开发者了解各服务的访问路径和职责范围。
 * </p>
 */
@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/gateway")
public class ServiceDirectoryController {

    /** 服务目录业务逻辑 */
    private final ServiceDirectoryService serviceDirectoryService;

    /**
     * 构造服务目录控制器
     *
     * @param serviceDirectoryService 服务目录服务
     */
    public ServiceDirectoryController(ServiceDirectoryService serviceDirectoryService) {
        this.serviceDirectoryService = serviceDirectoryService;
    }

    /**
     * 获取所有服务的目录列表
     *
     * @return 服务目录列表（包含服务编码、名称、路径、职责描述）
     */
    @GetMapping("/services")
    public ApiResponse<List<ServiceDirectoryDTO>> listServices() {
        return ApiResponse.success(serviceDirectoryService.listServices(), RequestContext.getRequestId());
    }
}
