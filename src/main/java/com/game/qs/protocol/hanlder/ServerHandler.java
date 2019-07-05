package com.game.qs.protocol.hanlder;

import com.game.qs.biz.router.MsgHandleRouter;
import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import com.game.qs.protocol.beartbeat.HeartBeatHandler;
import com.game.qs.protocol.beartbeat.ServerHeartBeatHandler;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.utils.SpringBeanFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Created by zun.wei on 2019/6/21.
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<AbstractNetMessage> {


    private OnlineUsers onlineUsers = OnlineUsers.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractNetMessage msg) throws Exception {
        MsgHandleRouter.route(ctx, msg);
    }

    //1
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    // 2
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    //3
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //Channel channel = ctx.channel();
        //Object name = channel.attr(GlobalConf.name).get();
        //System.out.println("name3 = " + name);

        // 用户加入
        OnlineUser user = new OnlineUser(ctx);

        //OnlineUserInfo onlineUserInfo = new OnlineUserInfo();
        //BeanUtils.copyProperties(user, onlineUserInfo);

        //BeansTool.getMongoTemplate().insert(onlineUserInfo);
        onlineUsers.put(ctx, user);
        super.channelActive(ctx);
    }

    //4 (两次)
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    // 5
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    // 6
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("定时检测客户端端是否存活");
                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandler.class);
                heartBeatHandler.process(ctx) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    // 7
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    // 8
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        onlineUsers.remove(ctx);
        super.handlerRemoved(ctx);
    }

}
