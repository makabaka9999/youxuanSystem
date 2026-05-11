package com.youxuan.common.constant;

public final class CacheKeyConstants {

    public static final String LOGIN_TOKEN_PREFIX = "youxuan:auth:login:";
    public static final String CAPTCHA_PREFIX = "youxuan:auth:captcha:";
    public static final String IDEMPOTENT_LOCK_PREFIX = "youxuan:idempotent:lock:";
    public static final String PRODUCT_DETAIL_PREFIX = "youxuan:product:detail:";
    public static final String CATEGORY_TREE = "youxuan:product:category:tree";

    private CacheKeyConstants() {
    }
}
