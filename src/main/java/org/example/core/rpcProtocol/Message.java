package org.example.core.rpcProtocol;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class Message implements Serializable {
    protected int code=0;
    protected int sequenceId;

    protected int messageType;
//    protected static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();
//    static {
//        messageClasses.put(1, RpcRequest.class);
//        messageClasses.put(2, RpcResponse.class);
//        messageClasses.put(3, Ping.class);
//
//    }
//    public static Class<? extends Message> getType(int messageType){
//        return messageClasses.get(messageType);
//    }
}
