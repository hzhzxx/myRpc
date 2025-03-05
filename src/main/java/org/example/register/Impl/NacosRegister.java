package org.example.register.Impl;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.example.properties.RpcProperties;
import org.example.register.Register;


import java.net.InetAddress;
import java.util.Properties;

import static org.example.common.constant.Prefix.REGISTE_RPREFIX;


public class NacosRegister extends CommonRegister implements Register {
    private RpcProperties rpcProperties;

    private NamingService namingService;


    public NacosRegister(RpcProperties rpcProperties,NamingService namingService){

        this.rpcProperties=rpcProperties;
        this.namingService=namingService;
    }
    @Override
    public void register(String serviceName, Object service){
        super.register(serviceName,service);
        try {
             // 服务名称
            String ip = InetAddress.getLocalHost().getHostAddress(); // 实例 IP
            int port = rpcProperties.getPort(); // 实例端口
            namingService.registerInstance(REGISTE_RPREFIX+serviceName, ip, port);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public static void main(String[] args) throws NacosException, InterruptedException {

        String serverAddr = "127.0.0.1:8848"; // Nacos 服务地址
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace","532e59d2-de74-43f4-a440-360c9058681d");

        try {
            NamingService namingService = NacosFactory.createNamingService(properties);
            // 注册当前服务实例
            // 服务名称
            String serviceName = "your-service-name";
            String ip = InetAddress.getLocalHost().getHostAddress(); // 实例 IP
            int port = 999; // 实例端口
            namingService.registerInstance(serviceName, ip, port);


            Thread.sleep(1000000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
