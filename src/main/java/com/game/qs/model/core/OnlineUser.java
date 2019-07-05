package com.game.qs.model.core;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zun.wei on 2019/6/26 11:08.
 * Description:
 */
public class OnlineUser implements Serializable {

    /**
     * socket连接
     */
    public ChannelHandlerContext ctx;

    /**
     * 访问时间MAP
     */
    public HashMap<String, Long> visitMap;

    /**
     * 重复请求次数
     */
    public int requestRepeatedNum = 0;

    /**
     * 最后一次请求时间
     */
    public int lastVisit = 0;

    /**
     * 玩家（角色）ID
     */
    public int uid;

    /**
     * 平台ID
     */
    public String platformId;

    /**
     * 玩家的位置缓存
     */
    //public HashMap<Integer, PlayerPosition> positionMap = null;

    /**
     * 玩家走的路径缓存
     */
    public List<Integer> roadBuffer = null;

    /**
     * 是否打坐中
     */
    public boolean meditating = false;

    /**
     * 最后请求方法
     */
    //public JSONObject lastRequest = null;

    /**
     *  是否登录
      */
    public boolean isLogin = false;

    /**
     * 构造函数
     *
     * @param ctx Socket连接
     */
    public OnlineUser(ChannelHandlerContext ctx) {
        if (ctx != null) {
            this.ctx = ctx;
            visitMap = new HashMap<String, Long>();
            lastVisit = (int) (System.currentTimeMillis() / 1000);

            roadBuffer = new ArrayList<Integer>();
            //positionMap = new HashMap<Integer, PlayerPosition>();
            //for (MapType type : MapType.values()) {
            //    positionMap.put(type.intValue(), null);
            //}

        }
    }

    /**
     * 发送数据到前端
     *
     * @param msg
     */
    public void write(String msg) {
        if (ctx == null) {
            return;
        }
        ctx.channel().write(msg);
        //BeansTool.getMongoTemplate().insert(msg);
        //MClientMsg.add(this, 2, msg);
    }

}
