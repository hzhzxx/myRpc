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

    // 错误类型
    private Exception exceptionValue=null;
    // 协议体部分
    private String body;
}

