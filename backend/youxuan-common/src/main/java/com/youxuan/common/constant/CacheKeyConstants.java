package com.youxuan.common.constant;

/**
 * Redis 缓存 Key 常量
 * <p>
 * 统一管理所有 Redis 缓存 Key 的前缀和完整 Key。
 * 采用冒号分隔的命名风格（如 youxuan:auth:login:xxx），便于在 Redis CLI 中按前缀搜索。
 * </p>
 */
public final class CacheKeyConstants {

    /** 登录 Token 缓存前缀：youxuan:auth:login:{userId} */
    public static final String LOGIN_TOKEN_PREFIX = "youxuan:auth:login:";
    /** 验证码缓存前缀：youxuan:auth:captcha:{captchaKey} */
    public static final String CAPTCHA_PREFIX = "youxuan:auth:captcha:";
    /** 幂等锁缓存前缀：youxuan:idempotent:lock:{idempotencyKey} */
    public static final String IDEMPOTENT_LOCK_PREFIX = "youxuan:idempotent:lock:";
    /** 商品详情缓存前缀：youxuan:product:detail:{productId} */
    public static final String PRODUCT_DETAIL_PREFIX = "youxuan:product:detail:";
    /** 类目树缓存完整 Key */
    public static final String CATEGORY_TREE = "youxuan:product:category:tree";

    /** 工具类，私有构造防止实例化 */
    private CacheKeyConstants() {
    }
}
