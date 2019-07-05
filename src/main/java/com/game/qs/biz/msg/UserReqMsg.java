package com.game.qs.biz.msg;

import com.game.qs.annotation.Command;
import com.game.qs.model.proto.PersonModel;
import com.game.qs.model.proto.UserModel;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.game.qs.biz.CommandDefinition.USER_REQ_MSG_COMMAND;

/**
 * Created by zun.wei on 2019/6/27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = USER_REQ_MSG_COMMAND)
public class UserReqMsg extends AbstractNetMessage {


    private int id;

    private String name;

    private String email;


    @Override
    public NetMessage buildMsgBody() {
        UserModel.UserReq.Builder builder = UserModel.UserReq.newBuilder();
        builder.setId(this.id).setName(this.name).setEmail(this.email);
        NetMessage netMessage = super.getNetMessage();
        netMessage.getMsgBody().setBytes(builder.build().toByteArray());
        return netMessage;
    }

    @Override
    public AbstractNetMessage decodeMsg(NetMessage netMessage)
            throws InvalidProtocolBufferException {
        MsgBody msgBody = netMessage.getMsgBody();
        MsgHeader msgHeader = netMessage.getMsgHeader();
        UserModel.UserReq person = UserModel.UserReq.parseFrom(msgBody.getBytes());
        return new UserReqMsg()
                .setId(person.getId())
                .setName(person.getName())
                .setEmail(person.getEmail())
                .setCmd(msgHeader.getCommand());
    }

}
