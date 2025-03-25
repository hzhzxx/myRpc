package org.example.core.server;


import lombok.Data;
import org.example.register.Register;
import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcRequestBody;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.common.util.GsonUtil;


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


            RpcRequest request= (RpcRequest) objectInputStream.readObject();;


            RpcRequestBody body=request.getBody();

            System.out.println("接口名称"+body.getInterfaceName());
            System.out.println("方法名称"+body.getMethodName());
            System.out.println("方法参数"+body.getParameters());
            System.out.println("参数类型"+body.getParamTypes());

            RpcResponse response=new RpcResponse();


            Object localService=register.getService(body.getInterfaceName());
            if(localService!=null){
                Method method=localService.getClass().getMethod(body.getMethodName(),body.getParamTypes());
                Object res=method.invoke(localService,body.getParameters());

                response.setBody(GsonUtil.getGson().toJson(res));
                response.setCode(200);
            }
            else{
                DefaultBody user=new DefaultBody();
                user.setCode(-1);
                user.setName("未找到服务");

                response.setBody(GsonUtil.getGson().toJson(user));
                response.setCode(404);
            }




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
