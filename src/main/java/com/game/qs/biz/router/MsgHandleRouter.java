package com.game.qs.biz.router;

import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.MsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

import static com.game.qs.biz.CommandDefinition.LOGIN_REQ_MSG_COMMAND;
import static com.game.qs.biz.CommandDefinition.LOGIN_RESP_MSG_COMMAND;
import static com.game.qs.protocol.message.MsgHandler.MSG_HANDLER_MAP;
import static com.game.qs.protocol.message.register.IMessageRegister.NET_COMMAND_MAP;

/**
 * Created by zun.wei on 2019/6/23.
 */
public interface MsgHandleRouter {

    /**
     *  路由消息
     * @param ctx ChannelHandlerContext
     * @param msg AbstractNetMessage
     */
    @SuppressWarnings("unchecked")
    static void route(ChannelHandlerContext ctx, AbstractNetMessage msg) {
        // 如果消息类型为注册，则直接忽略
        Short msgType = NET_COMMAND_MAP.get(msg.getClass());
        if (Objects.isNull(msgType)) return;

        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        if (Objects.isNull(onlineUser)) {
            String log = String.format("MsgHandleRouter route ctx id %s onlineUser is null",
                    ctx.channel().id().asShortText());
            System.out.println("log = " + log);
            ctx.close();
        }
        boolean isLogin = onlineUser.isLogin;
        // 如果未登录
        if (!isLogin) {
            // 如果是登录命令
            if (msgType == LOGIN_REQ_MSG_COMMAND) {
                MsgHandler loginHandler = MSG_HANDLER_MAP.get(LOGIN_REQ_MSG_COMMAND);
                loginHandler.handle(ctx, msg);
            }
            // 如果不是登录命令
            else {
                //  获取消息操作类型
                String log = String.format("MsgHandleRouter route ctx id %s not login",
                        ctx.channel().id().asShortText());
                System.out.println("log = " + log);
                ctx.close();
                //MsgHandler loginRespHandler = MSG_HANDLER_MAP.get(LOGIN_RESP_MSG_COMMAND);
                //loginRespHandler.handle(ctx, msg);
            }
        }
        // 已登录
        else {
            //  获取消息操作类型
            MsgHandler msgHandler = MSG_HANDLER_MAP.get(msgType);
            if (Objects.isNull(msgHandler)) {
                // 未注册
                throw new RuntimeException("msgType " + msgType + " msgHandler not register");
            } else {
                msgHandler.handle(ctx, msg);
            }
        }
    }

}
