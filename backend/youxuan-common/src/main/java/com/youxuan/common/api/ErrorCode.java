package com.youxuan.common.api;

import org.springframework.http.HttpStatus;

/**
 * 全局业务错误码枚举
 * <p>
 * 统一管理平台所有业务场景下的错误码、中文提示文本及对应的 HTTP 状态码。
 * 每新增一个业务错误场景，应在此处添加对应的枚举常量。
 * </p>
 */
public enum ErrorCode {
    /** 操作成功 */
    SUCCESS("SUCCESS", "success", HttpStatus.OK),
    /** 请求参数不合法（参数缺失、格式错误等） */
    PARAM_INVALID("PARAM_INVALID", "参数不合法", HttpStatus.BAD_REQUEST),
    /** 未携带有效的登录凭证 */
    AUTH_REQUIRED("AUTH_REQUIRED", "未登录", HttpStatus.UNAUTHORIZED),
    /** Token 已失效或被篡改 */
    TOKEN_INVALID("TOKEN_INVALID", "Token 无效", HttpStatus.UNAUTHORIZED),
    /** Token 已超过有效期 */
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token 已过期", HttpStatus.UNAUTHORIZED),
    /** 当前用户无操作权限 */
    PERMISSION_DENIED("PERMISSION_DENIED", "权限不足", HttpStatus.FORBIDDEN),
    /** 请求的资源不存在 */
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在", HttpStatus.NOT_FOUND),
    /** 幂等Key冲突，重复请求被拒绝（X-Idempotency-Key 已使用） */
    IDEMPOTENT_CONFLICT("IDEMPOTENT_CONFLICT", "幂等请求冲突", HttpStatus.CONFLICT),
    /** 业务状态不允许当前操作（如订单已支付不可再取消） */
    STATE_CONFLICT("STATE_CONFLICT", "当前状态不允许操作", HttpStatus.CONFLICT),
    /** 服务端内部未预期的异常 */
    INTERNAL_ERROR("INTERNAL_ERROR", "服务端异常", HttpStatus.INTERNAL_SERVER_ERROR);

    /** 业务状态码字符串 */
    private final String code;
    /** 中文提示文本 */
    private final String message;
    /** 对应的 HTTP 状态码 */
    private final HttpStatus httpStatus;

    /**
     * 构造错误码枚举
     *
     * @param code       业务状态码
     * @param message    中文提示文本
     * @param httpStatus HTTP 状态码
     */
    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /** @return 业务状态码 */
    public String getCode() {
        return code;
    }

    /** @return 中文提示文本 */
    public String getMessage() {
        return message;
    }

    /** @return 对应的 HTTP 状态码 */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
