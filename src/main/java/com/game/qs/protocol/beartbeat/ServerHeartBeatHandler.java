package com.game.qs.protocol.beartbeat;

import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by zun.wei on 2019/6/28 11:33.
 * Description: 服务端心跳检测
 */
@Slf4j
@Component
public class ServerHeartBeatHandler implements HeartBeatHandler {


    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        long heartBeatTime = 15; //15秒没有心跳包，强制下线
        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        log.info("lastReadTime = {}", onlineUser.lastVisit);
        long now = System.currentTimeMillis() / 1000;
        if (now -  onlineUser.lastVisit > heartBeatTime){
            log.info("ServerHeartBeatHandler remove client {}", ctx.channel().id().asShortText());
            OnlineUsers.getInstance().remove(ctx);
            ctx.close();
        }
    }

}
