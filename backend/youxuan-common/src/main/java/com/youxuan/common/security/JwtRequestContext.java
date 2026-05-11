package com.youxuan.common.security;

/**
 * 请求级别的 JWT 认证信息持有者，基于 ThreadLocal 实现。
 * 每个 HTTP 请求在其生命周期内可通过此类获取当前登录用户信息。
 */
public class JwtRequestContext {

    private static final ThreadLocal<JwtClaims> CONTEXT = new ThreadLocal<>();

    /**
     * 将认证信息绑定到当前线程。
     */
    public static void set(JwtClaims claims) {
        CONTEXT.set(claims);
    }

    /**
     * 获取当前请求的认证信息，未认证时返回 null。
     */
    public static JwtClaims get() {
        return CONTEXT.get();
    }

    /**
     * 请求结束后清理 ThreadLocal，防止内存泄漏。
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
