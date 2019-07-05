package com.game.qs.protocol.message;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2019/6/23.
 */
public interface MsgHandler<MSG> {

    /** 消息操作集合 **/
    Map<Short, MsgHandler> MSG_HANDLER_MAP = new ConcurrentHashMap<>();

    /**
     *  操作收到的消息
     * @param ctx ChannelHandlerContext
     * @param msg AbstractNetMessage
     */
    void handle(ChannelHandlerContext ctx, MSG msg);

}
