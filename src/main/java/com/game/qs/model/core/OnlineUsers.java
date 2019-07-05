package com.game.qs.model.core;

import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zun.wei on 2019/6/26 11:19.
 * Description:
 */
public class OnlineUsers extends ConcurrentHashMap<ChannelHandlerContext, OnlineUser> {

    /**
     * 单例
     */
    private static OnlineUsers instance;

    /**
     * 通过玩家ID找Player的Map
     */
    public ConcurrentHashMap<Integer, OnlineUser> uidUserMap = new ConcurrentHashMap<>();

    /**
     * 获取单例
     *
     * @return Players
     */
    public static OnlineUsers getInstance() {
        if (instance == null) {
            instance = new OnlineUsers();
        }
        return instance;
    }

    @Override
    public OnlineUser put(ChannelHandlerContext ctx, OnlineUser user) {
        return super.put(ctx, user);
    }

    public OnlineUser get(ChannelHandlerContext ctx) {
        return super.get(ctx);
    }

    public OnlineUser remove(ChannelHandlerContext ctx) {
        OnlineUser user = super.remove(ctx);
        if (Objects.nonNull(user)) {
            OnlineUser userTmp = getUserByUid(user.uid);
            if (Objects.nonNull(userTmp)) {
                if (ctx.equals(userTmp.ctx)) {
                    removeUser(user);
                }
            }
        }
        return user;
    }

    /**
     * 根据玩家ID获取Player对象
     *
     * @param uid
     * @return Player
     */
    public OnlineUser getUserByUid(int uid) {
        if (uid <= 0) {
            return null;
        }
        return uidUserMap.get(uid);
    }

    /**
     * 把uid>0的player放到uidPlayerMap中
     *
     * @param user
     */
    public void putUser(OnlineUser user) {
        if (user.uid > 0) {
            uidUserMap.put(user.uid, user);
        }
    }

    public void removeUser(OnlineUser user) {
        if (user.uid > 0) {
            uidUserMap.remove(user.uid);
        }
    }

}
