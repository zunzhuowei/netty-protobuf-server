package com.game.qs.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zun.wei on 2019/6/23.
 */
@Data
@Configuration
public class PropertiesConfig {

    // tcp 监听的端口
    @Value("${netty.server.tcp-port}")
    private int nettyServerTcpPort;

    // 是否启动客户端用测试
    @Value("${netty.server.enable-client}")
    private boolean nettyServerEnableClient;


}
