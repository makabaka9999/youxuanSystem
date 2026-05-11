package com.youxuan.common.constant;

/**
 * API 相关常量
 * <p>
 * 统一管理 API 路由前缀和 HTTP 请求头名称。
 * 所有服务应使用此处定义的常量，避免硬编码字符串。
 * </p>
 */
public final class ApiConstants {

    /** API 统一前缀路径：/api/v1 */
    public static final String API_PREFIX = "/api/v1";
    /** 请求追踪ID的请求头名称 */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    /** 幂等性Key的请求头名称，用于防重提交 */
    public static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    /** JWT Token 认证请求头名称 */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** 工具类，私有构造防止实例化 */
    private ApiConstants() {
    }
}
