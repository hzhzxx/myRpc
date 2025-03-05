package org.example.annotation;


import org.example.common.select.LoadBalancer;
import org.example.discovery.Discovery;
import org.example.core.proxy.ProxyFactory;
import org.example.register.Register;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public class RpcAnnotationInject implements BeanPostProcessor{
    private Register register;

    private Discovery discovery;
    private LoadBalancer loadBalancer;
    public RpcAnnotationInject(Register register, Discovery discovery, LoadBalancer loadBalancer){
        this.register=register;
        this.discovery=discovery;
        this.loadBalancer=loadBalancer;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if(bean.getClass().isAnnotationPresent(RpcService.class)){
            RpcService rpcService =bean.getClass().getAnnotation(RpcService.class);
            String serviceName=null;
            if(StringUtils.hasLength(rpcService.value())){
                serviceName= rpcService.value();
            }
            else {
                serviceName=bean.getClass().getName();
            }
            System.out.println("注册服务"+serviceName);
            register.register(serviceName,bean);
        }

        for (Field field:bean.getClass().getDeclaredFields()){

            if(field.isAnnotationPresent(RpcClient.class)){
                field.setAccessible(true);

                RpcClient rpcClient=field.getAnnotation(RpcClient.class);
                try {
                    if(StringUtils.hasLength(rpcClient.value())){
                        System.out.println("注入服务"+rpcClient.value()+field.getType());
                        field.set(bean, ProxyFactory.getJDKProxy(field.getType(),rpcClient.value(),discovery,loadBalancer));
                    }
                    else {
                        System.out.println("注入服务"+field.getClass().getName());
                        field.set(bean,ProxyFactory.getJDKProxy(field.getType(),discovery,loadBalancer));
                    }
                }
                catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

}
