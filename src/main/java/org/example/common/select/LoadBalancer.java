package org.example.common.select;

import java.lang.reflect.Method;
import java.util.List;

public interface LoadBalancer {
    String select(List<String> addresses, Method method, Object[] args);
}