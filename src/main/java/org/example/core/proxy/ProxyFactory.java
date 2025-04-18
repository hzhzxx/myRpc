package org.example.core.proxy;

import net.sf.cglib.proxy.Enhancer;
import org.example.common.select.LoadBalancer;
import org.example.core.RpcClient.Client;
import org.example.discovery.Discovery;


import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static <T> T getJDKProxy(Class<T> interfaceClass, String remoteServiceName, Discovery discovery, LoadBalancer loadBalancer, Client client){

        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new JDKProxyInvocationHandler(remoteServiceName,discovery,loadBalancer, client));
    }
    public static <T> T getJDKProxy(Class<T> interfaceClass, Discovery discovery, LoadBalancer loadBalancer, Client client){

        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new JDKProxyInvocationHandler(interfaceClass,discovery,loadBalancer, client));
    }
    public static <T> T getCGLIBProxy(Class<T> interfaceClass,String remoteServiceName, Discovery discovery){

        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(interfaceClass);

        enhancer.setCallback(new CGLIBProxyInterceptor<>(remoteServiceName,discovery));

        return (T)enhancer.create();

    }
    public static <T> T getCGLIBProxy(Class<T> interfaceClass, Discovery discovery){

        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(interfaceClass);

        enhancer.setCallback(new CGLIBProxyInterceptor<>(interfaceClass,discovery));

        return (T)enhancer.create();

    }

    public static void main(String[] args) {
//        UserService userService=getCGLIBProxy(UserService.class,discovery);
//        userService.getUserById(1);

    }
}
