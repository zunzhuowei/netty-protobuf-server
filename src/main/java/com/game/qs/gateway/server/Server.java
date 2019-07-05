package com.game.qs.gateway.server;

import com.game.qs.config.PropertiesConfig;
import com.game.qs.gateway.client.Client;
import com.game.qs.protocol.initializer.ServerChannelInitializer;
import com.game.qs.utils.ThreadNameFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Created by zun.wei on 2019/6/21.
 */
@Component
public class Server {


    @Resource
    private PropertiesConfig propertiesConfig;

    @Resource
    private Client client;

    // 单线程接收
    private EventLoopGroup bossGroup
            = new NioEventLoopGroup(1, new ThreadNameFactory("boss"));

    // 多线程处理
    // 当设置线程数等于0时候使用 DEFAULT_EVENT_LOOP_THREADS
    private EventLoopGroup workerGroup
            = new NioEventLoopGroup(0, new ThreadNameFactory("work"));

    private ChannelFuture cf;

    @PostConstruct
    public void start() {
        System.out.println("Server true = " + true);
        ServerBootstrap s = new ServerBootstrap();
        s.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_REUSEADDR, true) //重用地址
                // 接收包的最大值
                .childOption(ChannelOption.SO_RCVBUF, 65536)
                // 发送包的最大值
                .childOption(ChannelOption.SO_SNDBUF, 65536)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)

                // 池化的ByteBuf分配器，提供统一的ByteBuf分配服务；
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))  // heap buf 's better
                // 日志输出级别
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerChannelInitializer());
        try {
            cf = s.bind(propertiesConfig.getNettyServerTcpPort()).sync();
            // 这里会阻塞main线程，暂时先注释掉
            //cf.channel().closeFuture().sync();
            cf.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //client.start();
    }

    @PreDestroy
    public void stop() throws Exception {
        cf.channel().closeFuture().syncUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
