package com.game.qs.protocol.message.pack;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zun.wei on 2019/6/22.
 *  网络消息体
 */
@Data
@Accessors(chain = true)
public class MsgBody {

    /**
     * 存储数据
     */
    private byte[] bytes;


}
