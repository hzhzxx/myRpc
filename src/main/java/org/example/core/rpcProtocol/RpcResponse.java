package org.example.core.rpcProtocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse extends Message implements Serializable {


    private Exception exceptionValue=null;

    private String body;
}

