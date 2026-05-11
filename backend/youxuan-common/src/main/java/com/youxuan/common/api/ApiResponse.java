package com.youxuan.common.api;

/**
 * 统一API响应结果封装类
 * <p>
 * 整个平台统一的JSON响应格式，包含状态码(code)、提示信息(message)、请求追踪ID(requestId) 和 业务数据(data)。
 * 所有Controller接口均应返回此类型，确保前端能够以一致的方式解析响应。
 * </p>
 *
 * @param <T> 业务数据类型
 */
public final class ApiResponse<T> {

    /** 业务状态码，如 SUCCESS 表示成功 */
    private final String code;
    /** 提示信息，对应状态码的描述文本 */
    private final String message;
    /** 请求追踪ID，用于链路追踪和问题排查 */
    private final String requestId;
    /** 业务数据泛型载体 */
    private final T data;

    /**
     * 私有构造方法，禁止外部直接实例化，统一通过静态工厂方法创建
     *
     * @param code      业务状态码
     * @param message   提示信息
     * @param requestId 请求追踪ID
     * @param data      业务数据
     */
    private ApiResponse(String code, String message, String requestId, T data) {
        this.code = code;
        this.message = message;
        this.requestId = requestId;
        this.data = data;
    }

    /**
     * 操作成功 —— 带业务数据和请求追踪ID
     *
     * @param data      业务数据
     * @param requestId 请求追踪ID
     * @param <T>       业务数据类型
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(T data, String requestId) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), requestId, data);
    }

    /**
     * 操作成功 —— 仅带业务数据，无请求追踪ID
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功的响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), "", data);
    }

    /**
     * 操作失败 —— 通过 ErrorCode 枚举构造，携带请求追踪ID
     *
     * @param errorCode 错误码枚举
     * @param requestId 请求追踪ID
     * @param <T>       业务数据类型
     * @return 失败的响应对象（data 为 null）
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String requestId) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), requestId, null);
    }

    /**
     * 操作失败 —— 直接指定状态码和提示信息，携带请求追踪ID
     *
     * @param code      业务状态码
     * @param message   提示信息
     * @param requestId 请求追踪ID
     * @param <T>       业务数据类型
     * @return 失败的响应对象（data 为 null）
     */
    public static <T> ApiResponse<T> fail(String code, String message, String requestId) {
        return new ApiResponse<>(code, message, requestId, null);
    }

    /**
     * 服务端内部错误 —— 仅带状态码和提示信息，无请求追踪ID
     *
     * @param code    业务状态码
     * @param message 提示信息
     * @param <T>     业务数据类型
     * @return 错误的响应对象（data 为 null）
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, "", null);
    }

    /** @return 业务状态码 */
    public String getCode() {
        return code;
    }

    /** @return 提示信息 */
    public String getMessage() {
        return message;
    }

    /** @return 请求追踪ID */
    public String getRequestId() {
        return requestId;
    }

    /** @return 业务数据 */
    public T getData() {
        return data;
    }
}
