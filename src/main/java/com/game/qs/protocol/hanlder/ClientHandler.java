package com.game.qs.protocol.hanlder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.game.qs.biz.msg.HeartBeatMsg;
import com.game.qs.biz.msg.LoginReqMsg;
import com.game.qs.biz.msg.PersonMsg;
import com.game.qs.config.BeansTool;
import com.game.qs.config.GlobalConf;
import com.game.qs.gateway.client.ReConnectJob;
import com.game.qs.http.model.ResultObject;
import com.game.qs.http.model.User;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2019/6/21.
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<AbstractNetMessage> {

    private static ScheduledExecutorService scheduledExecutorService
            = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder()
            .setNameFormat("scheduled-%d")
            .setDaemon(true)
            .build());

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;

            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                HeartBeatMsg heartBeatMsg = new HeartBeatMsg().setHit(0);
                ctx.writeAndFlush(heartBeatMsg).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("IO error,close Channel");
                        future.channel().close();
                    }
                });
            }

        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractNetMessage msg) throws Exception {
        System.out.println("ClientHandler m = " + msg);
        //ctx.writeAndFlush(msg);
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
        new Thread(() -> {
            // 登录
            LoginReqMsg loginReqMsg = new LoginReqMsg()
                    .setCaptcha("验证码").setUsername("username").setPassword("password");
            ctx.writeAndFlush(loginReqMsg);

            int i = 0;
            for (; ; ) {
//                try {
//                    Thread.sleep(1);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                System.out.println("i = " + i);
                PersonMsg personMsg = new PersonMsg().setId(i)
                        .setEmail("asdfdffdasfads").setName("haah");
                ctx.writeAndFlush(personMsg);
                i++;
                break;
            }
        }).start();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开了，重新连接！");
        scheduledExecutorService.scheduleAtFixedRate
                (new ReConnectJob(ctx), 0, 10, TimeUnit.SECONDS);
    }

    private boolean testSend(ChannelHandlerContext ctx) throws IOException {
        OkHttpClient okHttpClient = BeansTool.getOkHttpClient();
        String url = "http://127.0.0.1:8080/mongodbController/query";
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody body = response.body();
        if (Objects.isNull(body)) return true;
        String json = body.string();
        ResultObject resultObject = JSON.parseObject(json, ResultObject.class);
        Object objectData = resultObject.getData();

        if (objectData instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) objectData;
            List<User> users = jsonArray.toJavaList(User.class);
            System.out.println("users = " + users);
            User user = users.get(0);
            Channel channel = ctx.channel();
            channel.attr(GlobalConf.name).set(user.getName());
            //channel.attr(GlobalConf.userId).set(user.getUserId());
        }

        if (objectData instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) objectData;
            System.out.println("jsonObject = " + jsonObject);
            User user = jsonObject.toJavaObject(User.class);
            System.out.println("user = " + user);
        }

        System.out.println("resultObject = " + resultObject);
        return false;
    }

}
