package com.game.qs.model.core;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by zun.wei on 2019/6/27.
 */
@Data
@Accessors
@Document(collection = "userInfo")
public class OnlineUserInfo {

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
     * 是否打坐中
     */
    public boolean meditating = false;

}
