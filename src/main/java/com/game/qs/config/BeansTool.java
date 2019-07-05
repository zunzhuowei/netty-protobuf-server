package com.game.qs.config;

import com.game.qs.utils.SpringBeanFactory;
import lombok.Data;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by zun.wei on 2019/6/26.
 */
@Data
@Accessors(chain = true)
public class BeansTool {

    private static MongoTemplate mongoTemplate;
    private static OkHttpClient okHttpClient;

    public static MongoTemplate getMongoTemplate() {
        if (Objects.isNull(mongoTemplate)) {
            mongoTemplate = SpringBeanFactory
                    .getBean("mongoTemplate", MongoTemplate.class);
        }
        return mongoTemplate;
    }

    public static OkHttpClient getOkHttpClient() {
        if (Objects.isNull(okHttpClient)) {
            okHttpClient = SpringBeanFactory
                    .getBean("okHttpClient", OkHttpClient.class);
        }
        if (Objects.isNull(okHttpClient)) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10,TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }


}
