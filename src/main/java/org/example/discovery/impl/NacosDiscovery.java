package org.example.discovery.impl;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.example.discovery.Discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.example.constant.Prefix.REGISTE_RPREFIX;


public class NacosDiscovery implements Discovery {
    private NamingService namingService;
    public NacosDiscovery(NamingService namingService){
        this.namingService=namingService;
    }
    @Override
    public List<String> getServiceAddress(String name) {
        List<Instance> instances=null;
        try {
            instances= namingService.selectInstances(REGISTE_RPREFIX+name,true);
        }
        catch (NacosException e){
            e.printStackTrace();
        }
        if(instances==null){
            return new ArrayList<>();
        }
        List<String> list=instances.stream().map((e)->{
            return e.getIp()+":"+e.getPort();
        }).collect(Collectors.toList());
        return list;
    }

    public static void main(String[] args) throws NacosException {
        String serverAddr = "127.0.0.1"+":"+8848; // Nacos 服务地址
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace","532e59d2-de74-43f4-a440-360c9058681d");
        NamingService namingService = null;
        namingService = NacosFactory.createNamingService(properties);
        List<Instance> instances = namingService.selectInstances("your-service-name", true);
        System.out.println(instances);
    }
}
