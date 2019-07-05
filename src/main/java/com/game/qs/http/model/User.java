package com.game.qs.http.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by zun.wei on 2019/6/26 17:53.
 * Description:
 */
@Data
@Accessors(chain = true)
@Document(collection="user")
public class User {

    private String userId;

    private String name;

    private String uclass;

    private String email;

    private Date birthday;

    private int age;

    private int dataStatus;

}
