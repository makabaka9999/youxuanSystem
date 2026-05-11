package com.youxuan.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 自动配置类
 * <p>
 * 当 classpath 下存在 {@link RedisTemplate} 类时自动生效。
 * 配置自定义的 RedisTemplate，使用 String 序列化 Key、JSON 序列化 Value，
 * 并注册幂等锁服务 Bean。
 * </p>
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class)
public class RedisAutoConfiguration {

    /**
     * 自定义 RedisTemplate
     * <p>
     * Key 和 HashKey 使用 String 序列化（便于阅读和调试）；
     * Value 和 HashValue 使用 Jackson JSON 序列化（支持存储任意对象类型）。
     * </p>
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 配置完成的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

        // Key 序列化：字符串方式，方便在 Redis CLI 中查看
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // Value 序列化：JSON 格式，支持复杂对象自动序列化/反序列化
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 注册幂等锁服务 Bean
     *
     * @param stringRedisTemplate String 类型的 Redis 操作模板
     * @return 幂等锁服务实例
     */
    @Bean
    public IdempotentLockService idempotentLockService(StringRedisTemplate stringRedisTemplate) {
        return new IdempotentLockService(stringRedisTemplate);
    }
}
