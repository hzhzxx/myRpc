package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "huang.rpc")
@EnableConfigurationProperties
@Data
public class RpcProperties {
    private String registerType="redis";

    private String loadBalancer="random";

    private int port=1200;

    private int redisHearBeat=10000;

    private int redisExpireTime=20000;

    private String nacosIp="127.0.0.1";

    private int nacosPort=8848;

    private String nacosNameSpace;


}
