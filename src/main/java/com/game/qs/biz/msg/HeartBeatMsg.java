package com.game.qs.biz.msg;

import com.game.qs.annotation.Command;
import com.game.qs.model.proto.HeartBeatModel;
import com.game.qs.model.proto.UserModel;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.game.qs.biz.CommandDefinition.HEARTBEAT_MSG_COMMAND;

/**
 * Created by zun.wei on 2019/6/27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = HEARTBEAT_MSG_COMMAND)
public class HeartBeatMsg extends AbstractNetMessage {

    private int hit;

    @Override
    public NetMessage buildMsgBody() {
        HeartBeatModel.HeartBeat.Builder builder = HeartBeatModel.HeartBeat.newBuilder();
        builder.setHit(this.hit);
        NetMessage netMessage = super.getNetMessage();
        netMessage.getMsgBody().setBytes(builder.build().toByteArray());
        return netMessage;
    }

    @Override
    public AbstractNetMessage decodeMsg(NetMessage netMessage)
            throws InvalidProtocolBufferException {
        MsgBody msgBody = netMessage.getMsgBody();
        MsgHeader msgHeader = netMessage.getMsgHeader();
        HeartBeatModel.HeartBeat person = HeartBeatModel.HeartBeat.parseFrom(msgBody.getBytes());
        return new HeartBeatMsg()
                .setHit(person.getHit())
                .setCmd(msgHeader.getCommand());
    }

}
