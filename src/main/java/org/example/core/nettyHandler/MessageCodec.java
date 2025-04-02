package org.example.core.nettyHandler;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.example.common.util.Serializer;
import org.example.core.rpcProtocol.Message;
import org.example.core.rpcProtocol.RpcResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消息编解码器
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf,Message> {
    private Serializer serializer;
    public MessageCodec(Serializer serializer){

        this.serializer=serializer;
    }


    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {

        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes(new byte[]{1, 2, 3, 4});
        out.writeByte(1);
        out.writeByte(0);
        byte[] bytes=null;
        try {
            bytes=serializer.serialize(msg);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        out.writeInt(bytes.length);
        out.writeBytes(bytes);

        outList.add(out);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();

        byte version = in.readByte();

        byte serializerType = in.readByte();

        int length = in.readInt();

        byte[] bytes = new byte[length];

        in.readBytes(bytes, 0, length);
        Message message=null;
        try {
           message=serializer.deserialize(RpcResponse.class,bytes);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, length);
        log.debug("{}", message);

        out.add(message);
    }
}