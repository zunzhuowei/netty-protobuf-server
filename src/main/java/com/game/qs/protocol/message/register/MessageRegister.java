package com.game.qs.protocol.message.register;

import com.game.qs.annotation.Command;
import com.game.qs.protocol.message.AbstractNetMessage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by zun.wei on 2019/6/22.
 * 消息注册者
 */
@Component
public class MessageRegister implements ApplicationContextAware, BeanFactoryPostProcessor, IMessageRegister {

    private static ApplicationContext context;

    // 是否使用 spring 注入方式注册消息了
    public static boolean isReg = false;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Map<String, Object> msgs = context.getBeansWithAnnotation(Command.class);
        msgs.values().parallelStream().forEach(e -> {
            if (e instanceof AbstractNetMessage) {
                AbstractNetMessage abstractNetMessage = (AbstractNetMessage) e;
                // 消息注册集合
                NET_MESSAGE_MAP.putIfAbsent(abstractNetMessage.getCmd(), abstractNetMessage);
                // 消息命令集合
                NET_COMMAND_MAP.putIfAbsent(abstractNetMessage.getClass(), abstractNetMessage.getCmd());
            }
        });
        isReg = true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
