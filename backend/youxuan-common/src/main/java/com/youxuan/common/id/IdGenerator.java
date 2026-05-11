package com.youxuan.common.id;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class IdGenerator {

    private static final int MAX_SEQUENCE = 999;
    private static final int MILLIS_SHIFT = 1000;

    private final AtomicInteger sequence = new AtomicInteger(0);

    public long nextId() {
        int currentSequence = sequence.updateAndGet(value -> value >= MAX_SEQUENCE ? 0 : value + 1);
        return System.currentTimeMillis() * MILLIS_SHIFT + currentSequence;
    }
}
