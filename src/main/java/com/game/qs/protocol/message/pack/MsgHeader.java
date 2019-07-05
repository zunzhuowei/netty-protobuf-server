package com.game.qs.protocol.message.pack;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zun.wei on 2019/6/22.
 * 网络消息头 (魔法头short + 版本号byte + 长度int + 命令short)
 */
@Data
@Accessors(chain = true)
public class MsgHeader {

    /**
     * 包头的长度
     */
    public static final int HEAD_LEN = 9;

    /**
     * 魔法头
     */
    private short head;//2 byte

    /**
     * 命令
     */
    private short command;//2 byte

    /**
     * 版本号
     */
    private byte version;//1 byte

    /**
     * 包体长度
     */
    private int len;//4 byte


}
