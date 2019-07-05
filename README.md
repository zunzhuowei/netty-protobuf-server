## netty-protobuf-server
基于 `netty` 构建的 `tcp` 长连接服务端，采用 `google` `protoBuf` 高速编码为底层传输，
并自定义编码、解码器。实现客户端的断线重连，服务端的心跳检测，接入 `mongodb` 作为储存（目前只是 demo）。

## 自定义编码、解码器

> 网络消息头 (魔法头short + 版本号byte + 长度int + 命令short)
> 网络消息体  byte[] bytes;

```java
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
```
## .proto 文件转java对象
编写 .proto 文件

```html
syntax = "proto3";
option java_package = "com.game.qs.model.proto";
option java_outer_classname = "LoginModel";

message LoginReq {
    string username = 1; // 用户名
    string password = 2; // 密码
    string captcha = 3; // 验证码
}

message LoginResp {
    int32 err = 1; // 错误码
    string msg = 2; // 消息
    string data = 3; // 数据
}
```

使用 `protobuf` 插件，执行 `protobuf:complie` 即可快速生成

## 服务端心跳检测
```java
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        
        //5 秒 没有收到客户端发送消息就发送心跳包
        p.addLast(new IdleStateHandler(5, 0, 0));
        
        p.addLast(new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast(new MessageProtoBufDecoder());
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast(new MessageProtoBufEncoder());
        p.addLast(new ServerHandler());
    }
}
```
```java
public class ServerHandler extends SimpleChannelInboundHandler<AbstractNetMessage> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.info("定时检测客户端端是否存活");
                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(ServerHeartBeatHandler.class);
                heartBeatHandler.process(ctx) ;
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
```
```java
public class ServerHeartBeatHandler implements HeartBeatHandler {
    @Override
    public void process(ChannelHandlerContext ctx) throws Exception {
        long heartBeatTime = 15; //15秒没有心跳包，强制下线
        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        log.info("lastReadTime = {}", onlineUser.lastVisit);
        long now = System.currentTimeMillis() / 1000;
        if (now -  onlineUser.lastVisit > heartBeatTime){
            log.info("ServerHeartBeatHandler remove client {}", ctx.channel().id().asShortText());
            OnlineUsers.getInstance().remove(ctx);
            ctx.close();
        }
    }
}
```
## 客户端心跳检测
```java
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        //3 秒没发送消息给服务器 将IdleStateHandler 添加到 ChannelPipeline 中
        p.addLast(new IdleStateHandler(0, 3, 0));
        
        p.addLast(new ProtobufVarint32FrameDecoder());
        ch.pipeline().addLast(new MessageProtoBufDecoder());
        p.addLast(new ProtobufVarint32LengthFieldPrepender());
        ch.pipeline().addLast(new MessageProtoBufEncoder());
        p.addLast(new ClientHandler());
    }
}
```
```java
public class ClientHandler extends SimpleChannelInboundHandler<AbstractNetMessage> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                HeartBeatMsg heartBeatMsg = new HeartBeatMsg().setHit(0);
                ctx.writeAndFlush(heartBeatMsg).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        log.error("IO error,close Channel");
                        future.channel().close();
                    }
                });
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
```

## 业务开发
### 1.定义消息类型
```java
package com.game.qs.biz;
/**
 * Created by zun.wei on 2019/6/23.
 *  命令类型定义
 */
public interface CommandDefinition {

    /** 人员类型消息定义 **/
    short PERSON_MSG_COMMAND = 1;

    /** 用户请求消息定义 **/
    short USER_REQ_MSG_COMMAND = 2;

    /** 用户请求返回消息定义 **/
    short USER_RESP_MSG_COMMAND = 3;

    /** 心跳包消息定义 **/
    short HEARTBEAT_MSG_COMMAND = 4;

    /** 登录请求消息定义 **/
    short LOGIN_REQ_MSG_COMMAND = 5;

    /** 登录响应消息定义 **/
    short LOGIN_RESP_MSG_COMMAND = 6;
}
```
### 2. 定义消息实体类
```java
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

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Command(cmd = LOGIN_REQ_MSG_COMMAND)
public class LoginReqMsg extends AbstractNetMessage {
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
```
### 3. 定义消息处理类
```java
package com.game.qs.biz.handler;
import com.game.qs.annotation.Handler;
import com.game.qs.biz.msg.LoginReqMsg;
import com.game.qs.model.core.OnlineUser;
import com.game.qs.model.core.OnlineUsers;
import com.game.qs.protocol.message.AbstractMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;
import static com.game.qs.biz.CommandDefinition.LOGIN_REQ_MSG_COMMAND;

@Slf4j
@Handler(cmd = LOGIN_REQ_MSG_COMMAND)
public class LoginMsgHandler extends AbstractMsgHandler<LoginReqMsg> {
    @Override
    public void handle(ChannelHandlerContext ctx, LoginReqMsg msg) {
        log.info("LoginMsgHandler from client id = {}", ctx.channel().id().asLongText());
        System.out.println("LoginMsgHandler = " + msg + ",cmd:" + msg.getCmd());
        OnlineUser onlineUser = OnlineUsers.getInstance().get(ctx);
        if (Objects.nonNull(onlineUser)) {
            onlineUser.isLogin = true;
            ctx.writeAndFlush(msg);
        } else {
            log.info("LoginMsgHandler handle OnlineUser is null ! {}", ctx.channel().id().asShortText());
            ctx.close();
        }
    }
}
```
