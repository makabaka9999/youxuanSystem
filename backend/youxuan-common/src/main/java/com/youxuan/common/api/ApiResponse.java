package com.youxuan.common.api;

public final class ApiResponse<T> {

    private final String code;
    private final String message;
    private final String requestId;
    private final T data;

    private ApiResponse(String code, String message, String requestId, T data) {
        this.code = code;
        this.message = message;
        this.requestId = requestId;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data, String requestId) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), requestId, data);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String requestId) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), requestId, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message, String requestId) {
        return new ApiResponse<>(code, message, requestId, null);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public T getData() {
        return data;
    }
}
