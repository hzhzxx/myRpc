package org.example.core.nettyHandler;



import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.common.util.GsonUtil;
import org.example.core.rpcProtocol.Ping;
import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcRequestBody;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.register.Register;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@ChannelHandler.Sharable
public class RpcServerMessageHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Register register;
    public RpcServerMessageHandler(Register register){
        super();
        this.register=register;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest message) {
        RpcResponse response=new RpcResponse();

        response.setSequenceId(message.getSequenceId());
        RpcRequestBody body=message.getBody();

        try {
            String serviceName=body.getInterfaceName();

            Object localService = register.getService(serviceName);
            Method method=localService.getClass().getMethod(body.getMethodName(),body.getParamTypes());
            Object res=method.invoke(localService,body.getParameters());

            response.setBody(GsonUtil.getGson().toJson(res));
            response.setCode(200);

        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错:"+e.getMessage()));
        }finally {

            ctx.writeAndFlush(response);
            ReferenceCountUtil.release(message);

        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
