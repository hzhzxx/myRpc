# MySimpleRPC

#### 介绍
 一个小型的RPC框架。底层使用Socket进行网络通信，可配置Redis或Nacos为注册中心。

#### 软件架构
1. SpringBoot（实现以配置文件的方式配置框架的一些属性，以注解的方式注入和发现服务）
2. 使用Socket（直接使用tcp在传输层为客户端和服务端实现通信）
3. 使用Redis与nacos作为注册中心供用户自定义配置使用

#### 可优化点
1. 服务治理：整个rpc的运行怎么可靠，客户端请求太多，提高吞吐量是一个问题......
2. 编解码：在我们的框架中的requestBody是直接用的Java序列化，反序列化跨平台访问是一个问题，而且编解码性能、编码后的字节数量也是很重要的一些东西，这些也需要解决
3. RPC协议：协议的字段有很多的，协议版本、传输方式、序列化方式、连接个数等等，都还可以进行一定的优化

#### 快速上手
  **1.生成本地Maven依赖包：从以上的git仓库地址clone代码到本地，然后进入到项目pom目录中，执行maven安装命令：** 
```
mvn clean install
```
 **2.服务提供者-消费者同时引入该maven依赖**
```
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>myrpcCommon</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```
 **3.服务提供者、消费者同时配置注册中心(请更换为自己的服务地址)，redis需要配置starter-data-redis的相关配置** 
```
huang:
  rpc:
    port: 1003
    register-type: nacos
    nacos-name-space: 532e59d2-de74-43f4-a440-360c9058681d
    nacos-ip: 127.0.0.1 #nacosIP地址
    nacos-port: 8848  #nacos端口
    redis-expire-time: #redis注册的过期时间，单位为ms
    redis-hear-beat: #心跳时间，单位为ms
#如果使用redis为注册中心
spring:
  redis:
    host: 127.0.0.1
    port: 6379
#    password: root
    timeout: 5s
      # 连接超时
    connect-timeout: 5s
```
 **4.服务提供者使用示例** 
```
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Override
    public String getOrderById(String id) {
        ......
    }
}
```
 **5.服务消费者示例** 
```
@RestController
public class ConsumerController {
    @ClientRPC("test") //"test"为远程服务名
    private OrderService orderService; //本地接口服务
    @RequestMapping("/order/{id}")
    public String MyTest(@PathVariable("id")String id){
        return orderService.getOrderById("xxxxx"); //直接调用
    }
}
```

#### RPC原理
RPC(Remote procedure call)远程过程调用，简单理解是本地需要某个服务，而具体的服务由另外一个独立的服务端提供，我们可以通过网络等其他方式通知到服务端执行对应的服务，然后返回我们关心的信息。
这时候，出现了一个问题：**我们通过网络通信的通信ip和端口怎么获取** 
##### 服务发现 
解决这个问题，可以采用第三方，即是专门来管理服务，一旦有服务请求来，就告诉对方所请求的服务的地址有哪些。服务注册及服务发现架构中主要有消费者，注册中心，提供者三方架构角色，消费者通过注册中心去订阅自己关心的服务，注册中心会将注册了的服务地址等相关信息返回给消费者，消费者再通过具体的协议将数据发送到socket中最后由网卡发送到网络上，最后得到服务提供者的响应数据。
![输入图片说明](assets/image2.png)
#### 实现
##### 编写RPC协议
RpcRequest和RpcResponse都是RPC协议，RPC协议包括header和body两部分，header我们用String表示，body我们用序列化后的byte[]流表示，这里的字节流的序列化的方式可以是Java的序列化方式，可以换成JSON序列化方式，在我们框架中直接使用Java的序列化方式。

然后body中被序列化的内容，因为是codec层的工作，放在了codec包中，RPCReuqest要调用一个方法，需要知道接口名、方法名、参数、参数类型，因此把这些东西放进RpcRequestBody中即可，后面把它序列化后房价RpcRequest的body字节流中；同理，RPCResponse 的body中，只需要一个被序列化后的Java Object即可。

##### 分析

##### 客户端实现（动态代理）

##### 服务端实现（反射调用）

![输入图片说明](assets/image.png)

