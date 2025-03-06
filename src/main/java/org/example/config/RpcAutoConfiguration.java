package org.example.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;

import org.example.annotation.RpcAnnotationInject;
import org.example.common.select.LoadBalancer;
import org.example.common.select.impl.ConsistentHashLoadBalancer;
import org.example.common.select.impl.RandomLoadBalancer;
import org.example.common.select.impl.RoundRobinLoadBalancer;
import org.example.discovery.Discovery;
import org.example.discovery.impl.NacosDiscovery;
import org.example.discovery.impl.RedisDiscovery;
import org.example.properties.RpcProperties;
import org.example.register.Impl.NacosRegister;
import org.example.register.Impl.RedisRegister;
import org.example.register.Register;
import org.example.core.server.RpcServer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class RpcAutoConfiguration {

    @Bean
    public RpcProperties rpcProperties(){
        return new RpcProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolExecutor threadPoolExecutor(){
         ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(5,
                20,
                200L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
         return threadPoolExecutor;
    }
    @Bean("register")
    @Conditional(OnRedisRegisterTypeCondition.class)
    public Register redisRegister(RedisTemplate redisTemplate, RpcProperties rpcProperties, ThreadPoolExecutor threadPoolExecutor){
        Register register=new RedisRegister(rpcProperties,redisTemplate,threadPoolExecutor);
        System.out.println("redis注册");
        return register;
    }

    @Bean
    @Conditional(OnNacosRegisterTypeCondition.class)
    public NamingService namingService(RpcProperties rpcProperties){
        String serverAddr = rpcProperties.getNacosIp()+":"+rpcProperties.getNacosPort(); // Nacos 服务地址
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        if(StringUtils.hasLength(rpcProperties.getNacosNameSpace())){
            properties.put("namespace",rpcProperties.getNacosNameSpace());
        }
        NamingService namingService = null;
        try {
            namingService = NacosFactory.createNamingService(properties);
        }catch (NacosException e){
            System.out.println("创建失败nacos namingService");
            e.printStackTrace();
        }
        return namingService;
    }
    @Bean("register")
    @ConditionalOnBean(NamingService.class)
    public Register nacosRegister(NamingService namingService,RpcProperties rpcProperties){
        System.out.println("nacos注册");
        Register register=new NacosRegister(rpcProperties,namingService);
        return register;
    }





    @Conditional(OnRedisRegisterTypeCondition.class)
    @Bean("discovery")
    public Discovery redisDiscovery(RedisTemplate redisTemplate, RpcProperties rpcProperties){

        Discovery discovery=new RedisDiscovery(rpcProperties,redisTemplate);
        System.out.println("redis发现");


        return discovery;
    }
    @ConditionalOnBean(NamingService.class)
    @Bean("discovery")
    public Discovery nacosDiscovery(NamingService namingService){

        Discovery discovery=new NacosDiscovery(namingService);
        System.out.println("nacos发现");


        return discovery;
    }
    @Bean
    public LoadBalancer loadBalancer(RpcProperties rpcProperties){
        if(rpcProperties.getLoadBalancer().equals("random")){
            return new RandomLoadBalancer();
        }
        if(rpcProperties.getLoadBalancer().equals("round")){
            return new RoundRobinLoadBalancer();
        }
        if(rpcProperties.getLoadBalancer().equals("hash")){
            return new ConsistentHashLoadBalancer();
        }
        return new RandomLoadBalancer();
    }
    @Bean
    public RpcAnnotationInject rpcAnnotationInject(Register register, Discovery discovery, LoadBalancer loadBalancer){
        return new RpcAnnotationInject(register,discovery,loadBalancer);
    }
    @Bean
    public RpcServer rpcServer(Register register, RpcProperties rpcProperties, ThreadPoolExecutor threadPoolExecutor){
        if(register instanceof RedisRegister){
            System.out.println("rpc 服务获得redisregister");
        } else if (register instanceof NacosRegister) {
            System.out.println("rpc 服务获得nacosRegister");
        }
        RpcServer rpcServer=new RpcServer(rpcProperties.getPort(),register,threadPoolExecutor);
        return rpcServer;
    }



}
