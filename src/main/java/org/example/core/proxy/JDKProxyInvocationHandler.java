package org.example.core.proxy;

import org.example.common.select.LoadBalancer;
import org.example.common.util.RequestIdGenerator;
import org.example.core.RpcClient.Client;
import org.example.core.RpcClient.RpcClient;
import org.example.common.constant.BodyType;

import org.example.discovery.Discovery;
import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcRequestBody;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.common.util.GsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JDKProxyInvocationHandler implements InvocationHandler {
    private String remoteServiceName;
    private Discovery discovery;
    private LoadBalancer loadBalancer;
    private Client client;
    public JDKProxyInvocationHandler(String remoteServiceName, Discovery discovery, LoadBalancer loadBalancer, Client client){
        this.remoteServiceName=remoteServiceName;
        this.discovery=discovery;
        this.loadBalancer=loadBalancer;
        this.client=client;
    }
    public JDKProxyInvocationHandler(Class interfaceClass,Discovery discovery,LoadBalancer loadBalancer, Client client){
        this(interfaceClass.getName(),discovery,loadBalancer,client);
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

        header.put("version",1);

        RpcRequest rpcRequest=RpcRequest.builder()

                .build();
        int sequenceId = RequestIdGenerator.generateRequestId();
        rpcRequest.setBody(rpcRequestBody);
        rpcRequest.setSequenceId(sequenceId);

        List<String> list=discovery.getServiceAddress(remoteServiceName);
        if(list==null||list.size()==0){
            throw new Exception("没有对应的服务实例"+remoteServiceName);
        }

        RpcResponse rpcResponse=send(rpcRequest,list,method,args);
        for(int i=0;i<3;i++){
            if(rpcResponse.getCode()!=200){
                rpcResponse=send(rpcRequest,list,method,args);

            }
            else{
                break;
            }
        }

        String responseBody=rpcResponse.getBody();
        return GsonUtil.getGson().fromJson(responseBody,method.getReturnType());
    }
    private RpcResponse send(RpcRequest rpcRequest,List<String> list,Method method, Object[] args){
        String selectedAddress = loadBalancer.select(list, method, args);
        String[] address = selectedAddress.split(":");


        return client.sendToInvocation(address[0],Integer.parseInt(address[1]),rpcRequest);
    }
}
