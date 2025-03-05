package org.example.discovery.impl;



import org.example.discovery.Discovery;
import org.example.properties.RpcProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.example.common.constant.Prefix.REGISTE_RPREFIX;


public class RedisDiscovery implements Discovery {

    private RpcProperties rpcProperties;
    private RedisTemplate<String,String> redisTemplate;
    public RedisDiscovery(RpcProperties rpcProperties,RedisTemplate<String, String> redisTemplate){
        this.rpcProperties=rpcProperties;
        this.redisTemplate=redisTemplate;
    }
    @Override
    public List<String> getServiceAddress(String name) {
        String key=REGISTE_RPREFIX+name;
        Long currentTime=System.currentTimeMillis();
        List<String> list= new ArrayList<>(redisTemplate.opsForZSet().rangeByScore(key,currentTime-rpcProperties.getRedisExpireTime(),currentTime));
        return list;
    }
}
