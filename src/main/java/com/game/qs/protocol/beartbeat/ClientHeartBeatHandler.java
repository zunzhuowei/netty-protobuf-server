package com.game.qs.protocol.beartbeat;

import com.game.qs.gateway.client.Client;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/6/28 11:33.
 * Description:
 */
@Slf4j
@Component
public class ClientHeartBeatHandler implements HeartBeatHandler {

    @Resource
    private Client client;

    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        //Client.main(null);
        // client.start();
    }
}
