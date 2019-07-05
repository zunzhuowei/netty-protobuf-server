package com.game.qs.biz.msg;

import com.game.qs.annotation.Command;
import com.game.qs.model.proto.LoginModel;
import com.game.qs.model.proto.UserModel;
import com.game.qs.protocol.message.AbstractNetMessage;
import com.game.qs.protocol.message.pack.MsgBody;
import com.game.qs.protocol.message.pack.MsgHeader;
import com.game.qs.protocol.message.pack.NetMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.game.qs.biz.CommandDefinition.LOGIN_REQ_MSG_COMMAND;
import static com.game.qs.biz.CommandDefinition.USER_REQ_MSG_COMMAND;

/**
 * Created by zun.wei on 2019/6/27.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = LOGIN_REQ_MSG_COMMAND)
public class LoginReqMsg extends AbstractNetMessage {

    /*
        string username = 1; // 用户名
    string password = 2; // 密码
    string captcha = 3; // 验证码
     */

    private String username;

    private String password;

    private String captcha;


    @Override
    public NetMessage buildMsgBody() {
        LoginModel.LoginReq.Builder builder = LoginModel.LoginReq.newBuilder();
        builder.setCaptcha(this.captcha).setUsername(this.username).setPassword(this.password);
        NetMessage netMessage = super.getNetMessage();
        netMessage.getMsgBody().setBytes(builder.build().toByteArray());
        return netMessage;
    }

    @Override
    public AbstractNetMessage decodeMsg(NetMessage netMessage)
            throws InvalidProtocolBufferException {
        MsgBody msgBody = netMessage.getMsgBody();
        MsgHeader msgHeader = netMessage.getMsgHeader();
        LoginModel.LoginReq person = LoginModel.LoginReq.parseFrom(msgBody.getBytes());
        return new LoginReqMsg()
                .setCaptcha(person.getCaptcha())
                .setPassword(person.getPassword())
                .setUsername(person.getUsername())
                .setCmd(msgHeader.getCommand());
    }

}
