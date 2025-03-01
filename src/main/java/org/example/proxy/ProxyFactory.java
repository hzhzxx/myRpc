package org.example.proxy;

import net.sf.cglib.proxy.Enhancer;
import org.example.discovery.Discovery;


import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static <T> T getJDKProxy(Class<T> interfaceClass, String remoteServiceName, Discovery discovery){

        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new JDKProxyInvocationHandler(remoteServiceName,discovery));
    }
    public static <T> T getJDKProxy(Class<T> interfaceClass, Discovery discovery){

        return (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new JDKProxyInvocationHandler(interfaceClass,discovery));
    }
    public static <T> T getCGLIBProxy(Class<T> interfaceClass,String remoteServiceName, Discovery discovery){
        //1.创建Enhancer
        Enhancer enhancer = new Enhancer();
        //2.传递目标对象的class
        enhancer.setSuperclass(interfaceClass);
        //3.设置回调操作
        enhancer.setCallback(new CGLIBProxyInterceptor<>(remoteServiceName,discovery));

        return (T)enhancer.create();

    }
    public static <T> T getCGLIBProxy(Class<T> interfaceClass, Discovery discovery){
        //1.创建Enhancer
        Enhancer enhancer = new Enhancer();
        //2.传递目标对象的class
        enhancer.setSuperclass(interfaceClass);
        //3.设置回调操作
        enhancer.setCallback(new CGLIBProxyInterceptor<>(interfaceClass,discovery));

        return (T)enhancer.create();

    }

    public static void main(String[] args) {
//        UserService userService=getCGLIBProxy(UserService.class,discovery);
//        userService.getUserById(1);

    }
}
