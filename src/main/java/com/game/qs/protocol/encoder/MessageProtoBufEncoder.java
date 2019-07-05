package com.game.qs.protocol.encoder;

import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.NetMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by zun.wei on 2019/6/22.
 */
public class MessageProtoBufEncoder extends MessageToByteEncoder<AbstractNetMessage> {


//    @Override
//    protected void encode(ChannelHandlerContext ctx, AbstractNetMessage msg, List<Object> out)
//            throws Exception {
//        ByteBuf byteBuf = NetMessage.encodeMsg(msg.buildMsgBody());
//        System.out.println("byteBuf = " + byteBuf);
//        out.add(byteBuf);
//    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, AbstractNetMessage msg,
                          ByteBuf out) throws Exception {
        ByteBuf byteBuf = NetMessage.encodeMsg(msg.buildMsgBody());
        // 写入消息主体
        out.writeBytes(byteBuf);
        out.discardReadBytes();//回收已读字节
    }
}
