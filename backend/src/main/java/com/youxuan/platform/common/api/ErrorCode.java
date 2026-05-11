package com.youxuan.platform.common.api;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    PARAM_INVALID("PARAM_INVALID", "参数不合法", HttpStatus.BAD_REQUEST),
    AUTH_REQUIRED("AUTH_REQUIRED", "未登录", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token 已过期", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("TOKEN_INVALID", "Token 无效", HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED("PERMISSION_DENIED", "权限不足", HttpStatus.FORBIDDEN),
    USER_DISABLED("USER_DISABLED", "用户已禁用", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源不存在", HttpStatus.NOT_FOUND),
    STATE_CONFLICT("STATE_CONFLICT", "当前状态不允许操作", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_ERROR", "服务端异常", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
