package com.game.qs.protocol.beartbeat;

import io.netty.channel.ChannelHandlerContext;

/**
 * Function:
 */
public interface HeartBeatHandler {

    /**
     * 处理心跳
     *
     * @param ctx
     * @throws Exception
     */
    void process(ChannelHandlerContext ctx) throws Exception;

}
