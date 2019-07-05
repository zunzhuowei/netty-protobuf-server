package com.game.qs.protocol.message.register;

import com.game.qs.protocol.message.AbstractNetMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2019/6/22.
 */
public interface IMessageRegister {

    /** 消息注册集合 **/
    Map<Short, AbstractNetMessage> NET_MESSAGE_MAP = new ConcurrentHashMap<>();

    /** 消息命令集合 **/
    Map<Class<? extends AbstractNetMessage>, Short> NET_COMMAND_MAP = new ConcurrentHashMap<>();

}
