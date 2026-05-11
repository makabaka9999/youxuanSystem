package com.youxuan.admin.service;

import com.youxuan.admin.model.ExceptionOrderDO;
import com.youxuan.admin.repository.ExceptionOrderRepository;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.api.PageResponse;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.security.JwtRequestContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 平台后台 - 异常管理服务（AdminExceptionService）。
 * <p>
 * 提供异常单的查询、处理和解决功能。当平台各业务环节
 * （订单、支付、退款等）出现异常时，系统自动或人工创建
 * 异常单，由平台运营人员跟进处理。
 * </p>
 */
@Service
public class AdminExceptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminExceptionService.class);

    private final ExceptionOrderRepository exceptionOrderRepository;

    public AdminExceptionService(ExceptionOrderRepository exceptionOrderRepository) {
        this.exceptionOrderRepository = exceptionOrderRepository;
    }

    /**
     * 分页查询异常单列表。
     *
     * @param exceptionType 异常类型（可选）
     * @param status        处理状态（可选）
     * @param pageNo        页码
     * @param pageSize      每页条数
     * @return 分页异常单列表
     */
    public PageResponse<ExceptionOrderDO> listExceptions(String exceptionType, String status, int pageNo, int pageSize) {
        List<ExceptionOrderDO> list;
        int total;
        if (exceptionType != null && !exceptionType.isEmpty()) {
            list = exceptionOrderRepository.findByType(exceptionType, pageNo, pageSize);
            total = exceptionOrderRepository.countByType(exceptionType);
        } else if (status != null && !status.isEmpty()) {
            list = exceptionOrderRepository.findByStatus(status, pageNo, pageSize);
            total = exceptionOrderRepository.countByStatus(status);
        } else {
            list = exceptionOrderRepository.findAll(pageNo, pageSize);
            total = exceptionOrderRepository.countAll();
        }
        return new PageResponse<>(pageNo, pageSize, total, list);
    }

    /**
     * 获取异常单详情。
     *
     * @param id 异常单 ID
     * @return 异常单数据对象
     */
    public ExceptionOrderDO getExceptionById(Long id) {
        return exceptionOrderRepository.findById(id);
    }

    /**
     * 处理异常单。
     * <p>
     * 平台运营人员对异常单进行处理，记录处理信息并更新状态为 PROCESSING。
     * </p>
     *
     * @param id          异常单 ID
     * @param handleResult 处理信息
     */
    @Transactional
    public void processException(Long id, String handleResult) {
        ExceptionOrderDO exception = exceptionOrderRepository.findById(id);
        if (exception == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "异常单不存在");
        }
        if (!"PENDING".equals(exception.getStatus())) {
            throw new BizException(ErrorCode.STATE_CONFLICT, "异常单状态不是待处理");
        }
        Long handledBy = JwtRequestContext.get() != null ? JwtRequestContext.get().getUserId() : null;
        exceptionOrderRepository.updateStatus(id, "PROCESSING", handleResult, handledBy);
        LOGGER.info("异常单 {} 开始处理", id);
    }

    /**
     * 解决异常单（标记为已解决）。
     *
     * @param id          异常单 ID
     * @param handleResult 处理结果说明
     */
    @Transactional
    public void resolveException(Long id, String handleResult) {
        ExceptionOrderDO exception = exceptionOrderRepository.findById(id);
        if (exception == null) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "异常单不存在");
        }
        Long handledBy = JwtRequestContext.get() != null ? JwtRequestContext.get().getUserId() : null;
        exceptionOrderRepository.updateStatus(id, "RESOLVED", handleResult, handledBy);
        LOGGER.info("异常单 {} 已解决", id);
    }
}
