package org.example.core.RpcClient;

import com.alibaba.nacos.api.exception.NacosException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.example.common.select.LoadBalancer;
import org.example.common.util.Serializer;
import org.example.core.nettyHandler.HeartBeatClientHandler;
import org.example.core.nettyHandler.MessageCodec;
import org.example.core.nettyHandler.ProcotolFrameDecoder;
import org.example.core.nettyHandler.RpcClientMessageHandler;
import org.example.core.rpcProtocol.RpcRequest;
import org.example.core.rpcProtocol.RpcResponse;
import org.example.discovery.Discovery;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;



@Slf4j
public class RpcClientNetty implements Client{


    private  Bootstrap bootstrap;
    private NioEventLoopGroup group;


    public   static Map<Integer, Promise<RpcResponse>> promises= new ConcurrentHashMap<Integer, Promise<RpcResponse>>();;

    private   Map<String, Channel> channels;
    private Serializer serializer;



    public RpcClientNetty(Serializer serializer) {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.channels = new ConcurrentHashMap<>();
        this.serializer=serializer;
        initChannel();

    }

    public  Channel get(String host,int port) {
        String key = host+":"+port;

        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channels != null && channel.isActive()) {
                return channel;
            }
            channels.remove(key);
        }


        synchronized (this){
            Channel channel = null;
            if (channels.containsKey(key)){
                return channels.get(key);
            }
            try {

                InetSocketAddress inetSocketAddress=new InetSocketAddress(host,port);
                channel = bootstrap.connect(inetSocketAddress).sync().channel();
                channel.closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        log.debug("断开连接");
                    }
                });
            } catch (InterruptedException e) {
                channel.close();
                log.debug("连接客户端出错" + e);
                return null;
            }
            channels.put(key, channel);
            return channel;
        }
    }

    // 初始化 channel 方法
    private  Bootstrap initChannel() {
        //日志
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        //编码
        MessageCodec MESSAGE_CODEC = new MessageCodec(serializer);
        //接收数据
        RpcClientMessageHandler RPC_HANDLER = new RpcClientMessageHandler();
        //心跳
        HeartBeatClientHandler HEATBEAT_CLIENT = new HeartBeatClientHandler();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(HEATBEAT_CLIENT);
                        ch.pipeline().addLast(RPC_HANDLER);

                    }
                });
        return bootstrap;
    }


    public RpcResponse sendToInvocation(String host, int port, RpcRequest request)  {
        Promise<RpcResponse> promise = new DefaultPromise<>(group.next());
        promises.put(request.getSequenceId(), promise);

        Channel channel = get(host,port);

        if (channel==null||!channel.isActive() || !channel.isRegistered()) {
            group.shutdownGracefully();
            return null;
        }

        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.debug("客户端发送消息成功");
            }
        });

        promise=promises.get(request.getSequenceId());
        try {
            promise.await();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return promise.getNow();
    }
}