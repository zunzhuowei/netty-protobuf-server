package com.game.qs.biz.handler;

import com.game.qs.annotation.Handler;
import com.game.qs.biz.msg.LoginReqMsg;
import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import com.game.qs.protocol.message.AbstractMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.game.qs.biz.CommandDefinition.LOGIN_REQ_MSG_COMMAND;


/**
 * Created by zun.wei on 2019/6/23.
 */
@Slf4j
@Handler(cmd = LOGIN_REQ_MSG_COMMAND)
public class LoginMsgHandler extends AbstractMsgHandler<LoginReqMsg> {


    @Override
    public void handle(ChannelHandlerContext ctx, LoginReqMsg msg) {
        log.info("LoginMsgHandler from client id = {}", ctx.channel().id().asLongText());
        System.out.println("LoginMsgHandler = " + msg + ",cmd:" + msg.getCmd());
        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        if (Objects.nonNull(onlineUser)) {
            onlineUser.isLogin = true;
            ctx.writeAndFlush(msg);
        } else {
            log.info("LoginMsgHandler handle OnlineUser is null ! {}", ctx.channel().id().asShortText());
            ctx.close();
        }
    }

}
