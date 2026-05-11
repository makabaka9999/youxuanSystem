package com.youxuan.common.id;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

/**
 * 分布式 ID 生成器
 * <p>
 * 基于时间戳 + 序列号的简单 ID 生成策略。
 * ID 格式：当前时间戳（毫秒）* 1000 + 自增序列号（0-999）。
 * 同一毫秒内最多生成 1000 个不重复 ID，超过后序列号回绕。
 * </p>
 */
@Component
public class IdGenerator {

    /** 序列号最大值（0-999），每毫秒最多 1000 个 ID */
    private static final int MAX_SEQUENCE = 999;
    /** 时间戳左移倍数，将时间戳和序列号拼接为一个 long */
    private static final int MILLIS_SHIFT = 1000;

    /** 自增序列号，线程安全 */
    private final AtomicInteger sequence = new AtomicInteger(0);

    /**
     * 生成下一个唯一 ID
     * <p>
     * 先对序列号做 CAS 递增，达到最大值后回绕到 0；
     * 然后将当前毫秒时间戳乘以 1000 与序列号相加，得到最终 ID。
     * </p>
     *
     * @return 全局趋势递增的唯一 ID
     */
    public long nextId() {
        // 线程安全地递增序列号，达到 MAX_SEQUENCE 后回绕到 0
        int currentSequence = sequence.updateAndGet(value -> value >= MAX_SEQUENCE ? 0 : value + 1);
        // 时间戳高位 + 序列号低位，组成唯一 ID
        return System.currentTimeMillis() * MILLIS_SHIFT + currentSequence;
    }
}
