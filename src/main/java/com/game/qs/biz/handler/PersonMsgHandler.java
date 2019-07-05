package com.game.qs.biz.handler;

import com.game.qs.annotation.Handler;
import com.game.qs.protocol.message.AbstractMsgHandler;
import com.game.qs.biz.msg.PersonMsg;
import io.netty.channel.ChannelHandlerContext;

import static com.game.qs.biz.CommandDefinition.PERSON_MSG_COMMAND;


/**
 * Created by zun.wei on 2019/6/23.
 */
@Handler(cmd = PERSON_MSG_COMMAND)
public class PersonMsgHandler extends AbstractMsgHandler<PersonMsg> {


    @Override
    public void handle(ChannelHandlerContext ctx, PersonMsg msg) {
        System.out.println("personMsg = " + msg + ",cmd:" + msg.getCmd());
        new Thread(() -> {
            PersonMsg personMsg = new PersonMsg().setId(100)
                    .setEmail("hehe").setName("15164");
            ctx.writeAndFlush(personMsg);
        }).start();
    }

}
