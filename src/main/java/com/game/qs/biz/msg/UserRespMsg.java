package com.game.qs.biz.msg;

import com.game.qs.annotation.Command;
import com.game.qs.model.proto.UserModel;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.game.qs.biz.CommandDefinition.USER_RESP_MSG_COMMAND;

/**
 * Created by zun.wei on 2019/6/27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = USER_RESP_MSG_COMMAND)
public class UserRespMsg extends AbstractNetMessage {

    private int err;
    private String msg;
    private String data;

    @Override
    public NetMessage buildMsgBody() {
        UserModel.UserResp.Builder builder = UserModel.UserResp.newBuilder();
        builder.setErr(this.err).setMsg(this.msg).setData(this.data);
        NetMessage netMessage = super.getNetMessage();
        netMessage.getMsgBody().setBytes(builder.build().toByteArray());
        return netMessage;
    }

    @Override
    public AbstractNetMessage decodeMsg(NetMessage netMessage)
            throws InvalidProtocolBufferException {
        MsgBody msgBody = netMessage.getMsgBody();
        MsgHeader msgHeader = netMessage.getMsgHeader();
        UserModel.UserResp person = UserModel.UserResp.parseFrom(msgBody.getBytes());
        return new UserRespMsg()
                .setErr(person.getErr())
                .setMsg(person.getMsg())
                .setData(person.getData())
                .setCmd(msgHeader.getCommand());
    }

}
