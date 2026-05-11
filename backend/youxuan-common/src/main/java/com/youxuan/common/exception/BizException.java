package com.youxuan.common.exception;

import com.youxuan.common.api.ErrorCode;

/**
 * 业务异常
 * <p>
 * 平台统一业务异常类。当业务校验不通过或操作被拒绝时，
 * 在 Service 层抛出此异常，由 {@link com.youxuan.common.web.GlobalExceptionHandler} 统一捕获并转换为标准 API 响应。
 * </p>
 */
public class BizException extends RuntimeException {

    /** 错误码枚举，关联对应的 HTTP 状态码和提示文本 */
    private final ErrorCode errorCode;

    /**
     * 构造业务异常，使用 ErrorCode 默认提示文本
     *
     * @param errorCode 错误码枚举
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常，使用自定义提示文本
     * <p>当需要比 ErrorCode 默认更详细的错误描述时使用此构造方法。</p>
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误描述
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /** @return 错误码枚举 */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
