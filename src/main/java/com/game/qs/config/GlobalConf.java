package com.game.qs.config;

import io.netty.util.AttributeKey;

/**
 * Created by zun.wei on 2019/6/27 16:27.
 * Description:
 */
public class GlobalConf {

    public static final AttributeKey<String> name = AttributeKey.valueOf("netty.channel.name"); //name
    public static final AttributeKey<String> userId = AttributeKey.valueOf("netty.channel.userId"); //userId

}
