package org.example.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnNacosRegisterTypeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取 RpcProperties 的值
        String registerType = context.getEnvironment().getProperty("huang.rpc.register-type", "redis");
        return "nacos".equals(registerType);
    }
}