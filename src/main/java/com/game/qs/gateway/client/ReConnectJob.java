package com.game.qs.gateway.client;

import com.game.qs.protocol.beartbeat.ClientHeartBeatHandler;
import com.game.qs.protocol.beartbeat.HeartBeatHandler;
import com.game.qs.utils.SpringBeanFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Function:
 */
@Slf4j
public class ReConnectJob implements Runnable {


    private ChannelHandlerContext context;

    private HeartBeatHandler heartBeatHandler;

    public ReConnectJob(ChannelHandlerContext context) {
        this.context = context;
        this.heartBeatHandler = SpringBeanFactory.getBean(ClientHeartBeatHandler.class);
    }

    @Override
    public void run() {
        try {
            if (Objects.isNull(heartBeatHandler))
                return;
            heartBeatHandler.process(context);
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }
}
