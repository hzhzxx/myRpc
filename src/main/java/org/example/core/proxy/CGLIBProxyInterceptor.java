package org.example.core.proxy;

import lombok.Data;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.core.RpcClient.RpcClient;
import org.example.common.constant.BodyType;

import org.example.discovery.Discovery;
import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcRequestBody;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.common.util.GsonUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CGLIBProxyInterceptor<T> implements MethodInterceptor {
    private String remoteServiceName;
    private Discovery discovery;

    private Random random=new Random();
    public CGLIBProxyInterceptor(String remoteServiceName, Discovery discovery){
        super();
        this.remoteServiceName=remoteServiceName;
        this.discovery=discovery;
    }
    public CGLIBProxyInterceptor(Class interfaceClass, Discovery discovery){
        this(interfaceClass.getName(),discovery);

    }


    @Override
    public Object intercept(Object oProxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        RpcRequestBody rpcRequestBody=RpcRequestBody.builder()
                .interfaceName(remoteServiceName)
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        Map<String,Object> header=new HashMap<>();
        header.put("body-type", BodyType.BYTEARRAYS);
        RpcRequest rpcRequest=RpcRequest.builder()

                .build();

        rpcRequest.setBody(rpcRequestBody);
        List<String> list=discovery.getServiceAddress(remoteServiceName);
        String[] address=list.get(random.nextInt(list.size())).split(":");

//        RpcResponse rpcResponse= RpcClient.sendToInvocation(address[0],Integer.parseInt(address[1]),rpcRequest);
        RpcResponse rpcResponse=new RpcResponse();
        Object responseBody=rpcResponse.getBody();


        return GsonUtil.fromObject(responseBody,method.getReturnType());
    }

    public static void main(String[] args) {
        System.out.println(new Random().nextInt(2));
        int i=Integer.parseInt("123");
        System.out.println(i);



    }
    @Data
    static class service{
        public void get(){
            System.out.println("代理成功");

        }
    }
}
