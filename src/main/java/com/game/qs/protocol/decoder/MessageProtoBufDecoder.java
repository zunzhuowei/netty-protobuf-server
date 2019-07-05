package com.game.qs.protocol.decoder;

import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.NetMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

import static com.game.qs.protocol.message.register.IMessageRegister.NET_MESSAGE_MAP;

/**
 * Created by zun.wei on 2019/6/22.
 */
@Slf4j
public class MessageProtoBufDecoder extends ByteToMessageDecoder {


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out)
            throws Exception {
        // 转码为网络消息
        NetMessage netMessage = NetMessage.decode(msg);
        if (Objects.nonNull(netMessage)) {
            // 从已注册的消息中获取消息实例
            AbstractNetMessage abstractNetMessage = NET_MESSAGE_MAP
                    .get(netMessage.getMsgHeader().getCommand());
            if (Objects.nonNull(abstractNetMessage)) {
                // 转码为系统消息并传给下游处理
                out.add(abstractNetMessage.decodeMsg(netMessage));
            } else {
                log.warn("MessageProtoBufDecoder abstractNetMessage is null !");
            }
        }
    }

}
