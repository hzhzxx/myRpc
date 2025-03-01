package org.example.proxy;

import lombok.Data;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.RpcClient.RpcClient;
import org.example.constant.BodyType;

import org.example.discovery.Discovery;
import org.example.rpcProtocol.RpcRequest;
import org.example.rpcProtocol.RpcRequestBody;
import org.example.rpcProtocol.RpcResponse;
import org.example.util.GsonUtil;

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
                .header(header)
                .build();

        rpcRequest.setRequestBody(rpcRequestBody);
        List<String> list=discovery.getServiceAddress(remoteServiceName);
        String[] address=list.get(random.nextInt(list.size())).split(":");

        RpcResponse rpcResponse= RpcClient.sendToInvocation(address[0],Integer.parseInt(address[1]),rpcRequest);
        Object responseBody=rpcResponse.getBody();
        System.out.println("调用状态"+rpcResponse.getHeader().get("code"));

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
