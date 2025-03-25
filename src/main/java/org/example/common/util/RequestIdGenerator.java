package org.example.common.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author PeterPan
 * @date 2023/8/18
 * @description
 */

public class RequestIdGenerator {
    private static final AtomicInteger counter = new AtomicInteger();

    public static int generateRequestId() {
        return counter.incrementAndGet();
    }
}

