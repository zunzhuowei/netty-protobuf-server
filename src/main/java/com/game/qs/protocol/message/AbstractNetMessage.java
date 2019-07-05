package com.game.qs.protocol.message;

import com.game.qs.annotation.Command;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.game.qs.protocol.message.register.MessageRegister;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.game.qs.protocol.message.register.IMessageRegister.NET_COMMAND_MAP;
import static com.game.qs.protocol.message.register.IMessageRegister.NET_MESSAGE_MAP;

/**
 * Created by zun.wei on 2019/6/22.
 */
@Data
@Accessors(chain = true)
public abstract class AbstractNetMessage implements INetMessage {


    private NetMessage netMessage;

    protected short cmd;

    protected AbstractNetMessage() {
        boolean b = this.getClass().isAnnotationPresent(Command.class);
        if (b) {
            short command = this.getClass()
                    .getAnnotation(Command.class)
                    .cmd();
            this.cmd = command;

            // 设置网络消息头值
            this.netMessage = new NetMessage()
                    // 设置消息头
                    .setMsgHeader(new MsgHeader()
                            .setHead(MAGIC_HEAD)
                            .setVersion(VERSION)
                            .setLen(0)
                            .setCommand(command))
                    // 设置消息体
                    .setMsgBody(new MsgBody());

            if (!MessageRegister.isReg) {
                // 消息注册集合
                NET_MESSAGE_MAP.putIfAbsent(this.cmd, this);
                // 消息命令集合
                NET_COMMAND_MAP.putIfAbsent(this.getClass(), this.cmd);
            }
        }
    }


}
