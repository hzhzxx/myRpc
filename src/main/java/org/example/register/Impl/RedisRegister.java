package org.example.register.Impl;


import org.example.properties.RpcProperties;
import org.example.register.Register;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static org.example.common.constant.Prefix.REGISTE_RPREFIX;


public class RedisRegister extends CommonRegister implements Register {

    private RedisTemplate redisTemplate;
    private RpcProperties rpcProperties;
    public RedisRegister(RpcProperties rpcProperties, RedisTemplate redisTemplate, ThreadPoolExecutor executor){
        this.rpcProperties=rpcProperties;
        this.redisTemplate=redisTemplate;
        CompletableFuture.runAsync(()->{
            while(true){
                this.heartbeat();
                try {
                    Thread.sleep(rpcProperties.getRedisHearBeat());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },executor);

    }
//    @Override
//    public void register2(String serviceName,Object service)  {
//        super.register(serviceName,service);
//        try {
//            String key=REGISTE_RPREFIX+serviceName;
//            String addressPort= InetAddress.getLocalHost().getHostAddress()+":"+port;
//            redisTemplate.boundSetOps(key).add(addressPort);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    @Override
    public void register(String serviceName,Object service)  {
        super.register(serviceName,service);
        updateOrAdd(serviceName);
    }
    private void updateOrAdd(String serviceName){
        try {
            String key=REGISTE_RPREFIX+serviceName;
            String addressPort= InetAddress.getLocalHost().getHostAddress()+":"+rpcProperties.getPort();
            redisTemplate.boundZSetOps(key).add(addressPort,System.currentTimeMillis());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void heartbeat(){
        List<String> serviceNames= new ArrayList<>(this.localRegister.keySet());
        for(String serviceName:serviceNames){
            updateOrAdd(serviceName);
        }
    }



}
