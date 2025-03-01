package org.example.proxy;

import org.example.RpcClient.RpcClient;
import org.example.constant.BodyType;

import org.example.discovery.Discovery;
import org.example.rpcProtocol.RpcRequest;
import org.example.rpcProtocol.RpcRequestBody;
import org.example.rpcProtocol.RpcResponse;
import org.example.util.GsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JDKProxyInvocationHandler implements InvocationHandler {
    private String remoteServiceName;
    private Discovery discovery;
    private Random random=new Random();
    public JDKProxyInvocationHandler(String remoteServiceName, Discovery discovery){
        this.remoteServiceName=remoteServiceName;
        this.discovery=discovery;
    }
    public JDKProxyInvocationHandler(Class interfaceClass,Discovery discovery){
        this(interfaceClass.getName(),discovery);
        System.out.println(interfaceClass.getName());
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequestBody rpcRequestBody= RpcRequestBody.builder()
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
        if(list==null||list.size()==0){
            throw new Exception("没有对应的服务实例"+remoteServiceName);
        }
        String[] address=list.get(random.nextInt(list.size())).split(":");

        RpcResponse rpcResponse= RpcClient.sendToInvocation(address[0],Integer.parseInt(address[1]),rpcRequest);
        Object responseBody=rpcResponse.getBody();
        System.out.println("调用状态"+rpcResponse.getHeader().get("code"));

        return GsonUtil.fromObject(responseBody,method.getReturnType());
    }
}
