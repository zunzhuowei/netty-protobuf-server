package com.game.qs.biz.handler;

import com.game.qs.annotation.Handler;
import com.game.qs.biz.msg.HeartBeatMsg;
import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import com.game.qs.protocol.message.AbstractMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import static com.game.qs.biz.CommandDefinition.HEARTBEAT_MSG_COMMAND;


/**
 * Created by zun.wei on 2019/6/23.
 */
@Slf4j
@Handler(cmd = HEARTBEAT_MSG_COMMAND)
public class HeartBeatMsgHandler extends AbstractMsgHandler<HeartBeatMsg> {


    @Override
    public void handle(ChannelHandlerContext ctx, HeartBeatMsg msg) {
        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        onlineUser.lastVisit = (int) (System.currentTimeMillis() / 1000);
        log.info("heartBeat from client id = {}", ctx.channel().id().asLongText());
        log.info("heartBeat = {},cmd:{},onlineUser.lastVisit:{}", msg, msg.getCmd(), onlineUser.lastVisit);
        ctx.writeAndFlush(msg);
    }

}
