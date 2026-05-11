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

@RestController
@RequestMapping(ApiConstants.API_PREFIX + "/gateway")
public class ServiceDirectoryController {

    private final ServiceDirectoryService serviceDirectoryService;

    public ServiceDirectoryController(ServiceDirectoryService serviceDirectoryService) {
        this.serviceDirectoryService = serviceDirectoryService;
    }

    @GetMapping("/services")
    public ApiResponse<List<ServiceDirectoryDTO>> listServices() {
        return ApiResponse.success(serviceDirectoryService.listServices(), RequestContext.getRequestId());
    }
}
