package com.youxuan.admin.service;

import com.youxuan.admin.model.OperationLogDO;
import com.youxuan.admin.repository.OperationLogRepository;
import com.youxuan.common.api.PageResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 平台后台 - 操作日志查询服务（AdminLogService）。
 * <p>
 * 提供操作审计日志的查询功能，平台管理员可按模块、
 * 操作类型等维度检索操作记录，用于安全审计和问题追溯。
 * </p>
 */
@Service
public class AdminLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminLogService.class);

    private final OperationLogRepository operationLogRepository;

    public AdminLogService(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    /**
     * 分页查询操作日志。
     *
     * @param moduleCode 模块编码（可选）
     * @param actionCode 操作编码（可选）
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @return 分页操作日志列表
     */
    public PageResponse<OperationLogDO> queryLogs(String moduleCode, String actionCode, int pageNo, int pageSize) {
        List<OperationLogDO> list = operationLogRepository.findByModuleAndAction(moduleCode, actionCode, pageNo, pageSize);
        int total = operationLogRepository.countByModuleAndAction(moduleCode, actionCode);
        LOGGER.info("查询操作日志，模块：{}，操作：{}，总数：{}", moduleCode, actionCode, total);
        return new PageResponse<>(pageNo, pageSize, total, list);
    }
}
