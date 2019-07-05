package com.game.qs.protocol.message;

import com.game.qs.annotation.Handler;

/**
 * Created by zun.wei on 2019/6/23.
 */
public abstract class AbstractMsgHandler<MSG> implements MsgHandler<MSG> {

    public AbstractMsgHandler() {
        boolean b = this.getClass().isAnnotationPresent(Handler.class);
        if (b) {
            Handler handler = this.getClass().getAnnotation(Handler.class);
            MSG_HANDLER_MAP.putIfAbsent(handler.cmd(), this);
        }
    }


}
