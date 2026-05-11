package com.youxuan.common.config;

import com.youxuan.common.constant.CacheKeyConstants;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 幂等性锁服务
 * <p>
 * 基于 Redis SET NX 命令实现的分布式锁，用于防重提交。
 * 关键写操作（如创建订单、支付回调）应使用 {@link #tryLock(String, Duration)} 确保同一幂等Key不会被执行两次。
 * </p>
 */
public class IdempotentLockService {

    /** Redis 操作模板，使用 String 序列化方式 */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 构造幂等锁服务
     *
     * @param stringRedisTemplate Redis 操作模板
     */
    public IdempotentLockService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试获取幂等锁
     * <p>
     * 使用 Redis SET NX 命令，仅当 key 不存在时设置成功。
     * 设置成功表示当前请求是首次执行；失败则表示重复请求。
     * </p>
     *
     * @param idempotencyKey 幂等Key（通常对应请求头 X-Idempotency-Key）
     * @param ttl            锁的生存时间，超时自动释放以防止死锁
     * @return true 表示获取锁成功（首次请求）；false 表示锁已存在（重复请求）
     */
    public boolean tryLock(String idempotencyKey, Duration ttl) {
        String redisKey = CacheKeyConstants.IDEMPOTENT_LOCK_PREFIX + idempotencyKey;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "LOCKED", ttl);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 主动释放幂等锁
     * <p>
     * 业务处理完成后调用，删除 Redis 中的锁记录。
     * 注意：即使不主动释放，锁也会在 TTL 到期后自动释放。
     * </p>
     *
     * @param idempotencyKey 幂等Key
     */
    public void unlock(String idempotencyKey) {
        stringRedisTemplate.delete(CacheKeyConstants.IDEMPOTENT_LOCK_PREFIX + idempotencyKey);
    }
}
