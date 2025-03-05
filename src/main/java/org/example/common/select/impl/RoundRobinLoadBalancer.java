package org.example.common.select.impl;

import org.example.common.select.LoadBalancer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String select(List<String> addresses, Method method, Object[] args) {
        int index = counter.getAndIncrement() % addresses.size();
        if (index < 0) { // 处理计数器溢出
            counter.set(0);
            index = 0;
        }
        return addresses.get(index);
    }
}