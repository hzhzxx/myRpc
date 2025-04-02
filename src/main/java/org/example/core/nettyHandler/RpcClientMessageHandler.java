package org.example.core.nettyHandler;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.example.core.RpcClient.RpcClientNetty;
import org.example.core.rpcProtocol.RpcResponse;


@Slf4j
@ChannelHandler.Sharable
public class RpcClientMessageHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg)  {
        try {
            log.debug("{}", msg);

            Promise<RpcResponse> promise = RpcClientNetty.promises.remove(msg.getSequenceId());

            if (promise != null) {

                int code=0;
                try {
                    code  = msg.getCode();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                if (code != 200) {
                    promise.setFailure(new Exception());
                } else {
                    promise.setSuccess(msg);
                }
            } else {
                promise.setFailure(new Exception("promise不存在"));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("出现异常"+cause);
        ctx.close();
    }
}
