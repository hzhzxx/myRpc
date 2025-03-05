package org.example.core.RpcClient;


import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.common.util.GsonUtil;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RpcClient {
    public static RpcResponse sendToInvocation(String host, int port, RpcRequest request){
        try {
            Socket socket=new Socket(host,port);

            OutputStream outputStream=socket.getOutputStream();
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);


            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);


            String requestStr=GsonUtil.getGson().toJson(request);


            objectOutputStream.writeObject(requestStr);
            objectOutputStream.flush();

            String responseStr= (String) objectInputStream.readObject();
            RpcResponse response=GsonUtil.getGson().fromJson(responseStr,RpcResponse.class);
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}
