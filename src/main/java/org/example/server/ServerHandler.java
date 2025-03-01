package org.example.server;


import lombok.Data;
import org.example.register.Register;
import org.example.rpcProtocol.RpcRequest;
import org.example.rpcProtocol.RpcRequestBody;
import org.example.rpcProtocol.RpcResponse;
import org.example.util.GsonUtil;


import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerHandler implements Runnable{


    private Socket socket;

    private Register register;
    public ServerHandler(Socket socket, Register register){
        this.socket=socket;
        this.register=register;
    }
    @Override
    public void run() {
        try {

            InputStream inputStream=socket.getInputStream();
            ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);

            OutputStream outputStream=socket.getOutputStream();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);

            String requestStr= (String) objectInputStream.readObject();
            System.out.println(requestStr);
            RpcRequest request= GsonUtil.getGson().fromJson(requestStr, RpcRequest.class);
            Map<String,Object> header=request.getHeader();

            RpcRequestBody body=request.getRequestBody();
            System.out.println(header.get("body-type"));
            System.out.println("接口名称"+body.getInterfaceName());
            System.out.println("方法名称"+body.getMethodName());
            System.out.println("方法参数"+body.getParameters());
            System.out.println("参数类型"+body.getParamTypes());

            RpcResponse response=new RpcResponse();
            Map<String,Object> responseHeader=new HashMap<>();

            Object localService=register.getService(body.getInterfaceName());
            if(localService!=null){
                Method method=localService.getClass().getMethod(body.getMethodName(),body.getParamTypes());
                Object res=method.invoke(localService,body.getParameters());
                response.setBody(res);
                responseHeader.put("code","200");
            }
            else{
                DefaultBody user=new DefaultBody();
                user.setCode(-1);
                user.setName("未找到服务");

                response.setBody(user);
                responseHeader.put("code","404");
            }

            response.setHeader(responseHeader);


            String responseStr=GsonUtil.getGson().toJson(response);
            objectOutputStream.writeObject(responseStr);
            objectOutputStream.flush();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Data
    private static class DefaultBody{
        private String name;
        private int code;
    }
}
