package org.example.test;

import org.example.common.util.Serializer;
import org.example.core.rpcProtocol.Message;
import org.example.core.rpcProtocol.RpcResponse;

import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
        RpcResponse message=new RpcResponse();
        message.setMessageType(1);
        message.setBody(new String(("12312")));

        message.setCode(200);
        message.setSequenceId(1);
        message.setExceptionValue(new Exception());
        System.out.println(message);
        Message m=message;
//        byte[] bytes= Serializer.serialize(m);
//        System.out.println(bytes);

    }
}
