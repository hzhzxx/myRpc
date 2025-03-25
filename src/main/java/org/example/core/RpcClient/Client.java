package org.example.core.RpcClient;

import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcResponse;

public interface Client {
    RpcResponse sendToInvocation(String host, int port, RpcRequest request);
}
