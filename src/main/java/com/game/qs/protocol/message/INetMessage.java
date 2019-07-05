package com.game.qs.protocol.message;

import com.game.qs.protocol.message.pack.NetMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Created by zun.wei on 2019/6/22.
 *  消息类型接口
 */
public interface INetMessage {

    /** 消息魔法头 **/
    short MAGIC_HEAD = 0x2425;

    /** 消息版本号 **/
    byte VERSION = 1;

    /**
     *  构建消息体
     */
    NetMessage buildMsgBody();

    /**
     *  解码成消息
     */
    AbstractNetMessage decodeMsg(NetMessage netMessage)
            throws InvalidProtocolBufferException;

}
