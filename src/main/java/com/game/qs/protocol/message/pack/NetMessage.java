package com.game.qs.protocol.message.pack;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.game.qs.protocol.message.INetMessage.MAGIC_HEAD;
import static com.game.qs.protocol.message.INetMessage.VERSION;

/**
 * Created by zun.wei on 2019/6/22.
 */
@Data
@Accessors(chain = true)
public class NetMessage {


    private MsgHeader msgHeader;

    private MsgBody msgBody;


    /**
     * 编码消息
     */
    public static ByteBuf encodeMsg(NetMessage netMessage) {
        MsgHeader msgHeader = netMessage.getMsgHeader();
        MsgBody msgBody = netMessage.getMsgBody();
        msgHeader.setLen(msgBody.getBytes().length); //包体占用的长度

        // 构建消息头
        short head = msgHeader.getHead();//short 2
        byte version = msgHeader.getVersion();//byte 1
        int len = msgHeader.getLen();//int 4
        short command = msgHeader.getCommand();//short 2

        ByteBuf byteBuf = Unpooled.buffer(len);

        // 写消息头
        byteBuf.writeShort(head);
        byteBuf.writeByte(version);
        byteBuf.writeInt(len);
        byteBuf.writeShort(command);

        // 写消息体
        byteBuf.writeBytes(msgBody.getBytes());

        return byteBuf;
    }

    /**
     * 解码消息
     */
    public static NetMessage decode(ByteBuf byteBuf) {
        int byteLength = byteBuf.readableBytes();
        // 校验消息长度
        if (byteLength < MsgHeader.HEAD_LEN) {
            return null;
        }

        //byteBuf.markReaderIndex();

        // 读消息头信息
        // Check the magic number.
        short magic = byteBuf.getShort(0);//2
        if (magic != MAGIC_HEAD) {
            //byteBuf.resetReaderIndex();
            //throw new CorruptedFrameException("Invalid magic number: " + MAGIC_HEAD);
            byteBuf.clear();
            return null;
        }

        //校验版本号
        byte v = byteBuf.getByte(2);//1
        if (v != VERSION) {
            byteBuf.clear();
            return null;
        }

        // 包体长度
        int bodyLen = byteBuf.getInt(3);//4
        //System.out.println("bodyLen = " + bodyLen + ",byteLength =" + byteLength);
        // 当接收到的包的长度不足一个包的时候，不接收，等下回来包满了再处理
        if (byteLength < bodyLen + MsgHeader.HEAD_LEN) {
            return null;
        }

        // 包体过长则不读取
        if (bodyLen > 10240) {
            // 当数据长度大于10K时，清空缓冲区
            byteBuf.clear();
            return null;
        }

        short head = byteBuf.readShort();//2
        byte version = byteBuf.readByte();//1
        int len = byteBuf.readInt();//4
        short cmd = byteBuf.readShort();//2

        // 读消息体
        byte[] body = new byte[len];
        byteBuf.readBytes(body);
        byteBuf.discardReadBytes();//回收已读字节

        // 构建网络消息头、消息体
        MsgHeader msgHeader = new MsgHeader().setHead(head)
                .setVersion(version).setLen(len).setCommand(cmd);
        MsgBody msgBody = new MsgBody().setBytes(body);

        return new NetMessage().setMsgHeader(msgHeader).setMsgBody(msgBody);
    }
}
