package org.example.common.select.impl;

import org.example.common.select.LoadBalancer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String select(List<String> addresses, Method method, Object[] args) {
        int index = ThreadLocalRandom.current().nextInt(addresses.size());
        return addresses.get(index);
    }
}