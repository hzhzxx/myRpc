package org.example.core.server;

import com.alibaba.nacos.shaded.io.grpc.ServerRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.common.util.Serializer;
import org.example.core.nettyHandler.MessageCodec;
import org.example.core.nettyHandler.MessageServerCodec;
import org.example.core.nettyHandler.ProcotolFrameDecoder;
import org.example.core.nettyHandler.RpcServerMessageHandler;
import org.example.register.Register;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RpcServerNetty implements Server{


    protected int port;
    protected Register register;

    NioEventLoopGroup worker = new NioEventLoopGroup();
    NioEventLoopGroup boss = new NioEventLoopGroup();
    ServerBootstrap bootstrap = new ServerBootstrap();
    private Serializer serializer;

    public RpcServerNetty(int port, Register register, ThreadPoolExecutor threadPoolExecutor, Serializer serializer) {
        this.register=register;
        this.port = port;
        this.serializer=serializer;
        CompletableFuture.runAsync(()->this.start(),threadPoolExecutor);

    }


    public void start() {

        LoggingHandler LOGGING = new LoggingHandler(LogLevel.DEBUG);

        MessageServerCodec MESSAGE_CODEC = new MessageServerCodec(serializer);

        RpcServerMessageHandler RPC_HANDLER = new RpcServerMessageHandler(register);

        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new ProcotolFrameDecoder());//定长解码器
                            pipeline.addLast(MESSAGE_CODEC);
                            pipeline.addLast(LOGGING);
                            pipeline.addLast(RPC_HANDLER);
                        }
                    });
            //绑定端口
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("netty服务已启动："+port);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("启动服务出错");
        }finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }



}
