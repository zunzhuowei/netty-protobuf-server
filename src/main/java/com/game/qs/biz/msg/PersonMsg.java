package com.game.qs.biz.msg;

import com.game.qs.annotation.Command;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.game.qs.model.proto.PersonModel;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.game.qs.biz.CommandDefinition.PERSON_MSG_COMMAND;


/**
 * Created by zun.wei on 2019/6/22.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = PERSON_MSG_COMMAND)
public class PersonMsg extends AbstractNetMessage {


    private int id;

    private String name;

    private String email;


    @Override
    public NetMessage buildMsgBody() {
        PersonModel.Person.Builder builder = PersonModel.Person.newBuilder();
        builder.setId(this.id).setName(this.name).setEmail(this.email);
        NetMessage netMessage = super.getNetMessage();
        netMessage.getMsgBody().setBytes(builder.build().toByteArray());
        return netMessage;
    }

    @Override
    public AbstractNetMessage decodeMsg(NetMessage netMessage) throws InvalidProtocolBufferException {
        MsgBody msgBody = netMessage.getMsgBody();
        MsgHeader msgHeader = netMessage.getMsgHeader();
        
        PersonModel.Person person = PersonModel.Person.parseFrom(msgBody.getBytes());
        return new PersonMsg()
                .setId(person.getId())
                .setName(person.getName())
                .setEmail(person.getEmail())
                .setCmd(msgHeader.getCommand());
    }

}
