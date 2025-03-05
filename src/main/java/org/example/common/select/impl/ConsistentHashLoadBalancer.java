package org.example.common.select.impl;

import org.example.common.select.LoadBalancer;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    private static final Function<Object[], String> DEFAULT_KEY_EXTRACTOR = args -> {
        if (args != null && args.length > 0) {
            return args[0].toString();
        }
        return "default_hash_key";
    };

    private final Function<Object[], String> keyExtractor;

    public ConsistentHashLoadBalancer() {
        this(DEFAULT_KEY_EXTRACTOR);
    }

    public ConsistentHashLoadBalancer(Function<Object[], String> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    @Override
    public String select(List<String> addresses, Method method, Object[] args) {
        String hashKey = keyExtractor.apply(args);
        int hashCode = hashKey.hashCode();
        int index = Math.abs(hashCode) % addresses.size();
        return addresses.get(index);
    }
}