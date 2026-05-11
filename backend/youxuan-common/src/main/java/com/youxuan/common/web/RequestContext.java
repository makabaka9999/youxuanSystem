package com.youxuan.common.web;

/**
 * 请求上下文持有器
 * <p>
 * 基于 ThreadLocal 存储当前线程（即当前请求）的上下文信息，主要用于传递请求追踪 ID。
 * 每个 HTTP 请求由 {@link RequestIdFilter} 在入口处设置，在请求结束后清理。
 * </p>
 */
public final class RequestContext {

    /** 存储当前线程的请求追踪 ID */
    private static final ThreadLocal<String> REQUEST_ID_HOLDER = new ThreadLocal<>();

    /** 工具类，私有构造防止实例化 */
    private RequestContext() {
    }

    /**
     * 设置当前请求的追踪 ID
     *
     * @param requestId 请求追踪 ID
     */
    public static void setRequestId(String requestId) {
        REQUEST_ID_HOLDER.set(requestId);
    }

    /**
     * 获取当前请求的追踪 ID
     *
     * @return 当前线程绑定的请求追踪 ID，可能为 null
     */
    public static String getRequestId() {
        return REQUEST_ID_HOLDER.get();
    }

    /**
     * 清理当前线程的上下文
     * <p>必须在请求结束时调用，防止 ThreadLocal 内存泄漏。</p>
     */
    public static void clear() {
        REQUEST_ID_HOLDER.remove();
    }
}
