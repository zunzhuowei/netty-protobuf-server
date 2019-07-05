package com.game.qs.biz.router;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zun.wei on 2019/6/23.
 */
public interface ExceptionHandleRouter {

    void route(ChannelHandlerContext ctx, Throwable cause);

}
