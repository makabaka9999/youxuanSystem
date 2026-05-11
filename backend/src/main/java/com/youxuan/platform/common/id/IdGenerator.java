package com.youxuan.platform.common.id;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private final AtomicInteger sequence = new AtomicInteger(0);

    public long nextId() {
        long millis = System.currentTimeMillis();
        int seq = sequence.updateAndGet(value -> value >= 999 ? 0 : value + 1);
        return millis * 1000 + seq;
    }
}
