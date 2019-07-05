package com.game.qs.gateway.client;

import com.game.qs.config.PropertiesConfig;
import com.game.qs.protocol.initializer.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/6/21.
 * 用于测试的客户端
 */
@Component
public class Client {


    @Resource
    private PropertiesConfig propertiesConfig;

    public static void main(String[] args) {
        start(9989);
    }

    public void start() {
        boolean enableClient = propertiesConfig.isNettyServerEnableClient();
        // 新启线程，阻塞main线程
        if (enableClient) {
            start(propertiesConfig.getNettyServerTcpPort());
        }
    }

    private static void start(int port) {
        new Thread(() ->
        {
            System.out.println("Client true = " + true);
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientChannelInitializer());
            try {
                ChannelFuture f = b.connect("127.0.0.1", port).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                // 断线重连
                try {
                    Thread.sleep(10000);
                    start(port);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
