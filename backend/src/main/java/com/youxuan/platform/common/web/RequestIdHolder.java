package com.youxuan.platform.common.web;

public final class RequestIdHolder {

    public static final String HEADER_NAME = "X-Request-Id";
    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    private RequestIdHolder() {
    }

    public static void set(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String get() {
        return REQUEST_ID.get();
    }

    public static void clear() {
        REQUEST_ID.remove();
    }
}
