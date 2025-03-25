package org.example.core.rpcProtocol;

import lombok.Builder;
import lombok.Data;

import java.io.*;
import java.util.Map;

@Data
@Builder
// Serializable：对象变成可传输的字节序列
public class RpcRequest extends Message  implements Serializable {


    private RpcRequestBody body;

//    public void setRequestBody(RpcRequestBody rpcRequestBody) {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//            oos.writeObject(rpcRequestBody);
//            this.body = baos.toByteArray();
//        } catch (IOException var4) {
//            throw new RuntimeException(var4);
//        }
//    }
//
//    public RpcRequestBody getRequestBody() throws IOException, ClassNotFoundException {
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
//        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
//        RpcRequestBody requestBody = (RpcRequestBody)objectInputStream.readObject();
//        return requestBody;
//    }


}

