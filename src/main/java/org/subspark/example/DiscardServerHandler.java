package org.subspark.example;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       ByteBuf in = (ByteBuf) msg;
       byte b;
       char ch;
       try {
           while (in.isReadable()) {
               b = in.readByte();
               ch = (char) b;
               System.out.println("" + ch + " " + b);
           }
       } finally {
           ReferenceCountUtil.release(msg);
       }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
