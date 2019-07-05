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
