package com.youxuan.common.config;

import com.youxuan.common.constant.CacheKeyConstants;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;

public class IdempotentLockService {

    private final StringRedisTemplate stringRedisTemplate;

    public IdempotentLockService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean tryLock(String idempotencyKey, Duration ttl) {
        String redisKey = CacheKeyConstants.IDEMPOTENT_LOCK_PREFIX + idempotencyKey;
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "LOCKED", ttl);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String idempotencyKey) {
        stringRedisTemplate.delete(CacheKeyConstants.IDEMPOTENT_LOCK_PREFIX + idempotencyKey);
    }
}
