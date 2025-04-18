package org.example.core.nettyHandler;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class ProcotolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProcotolFrameDecoder() {
        this(1024, 6, 4, 0, 0);
    }

    public ProcotolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
