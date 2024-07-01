package com.tratsiak.telegrambotmvc.core.dispatcher.impl;


import com.tratsiak.telegrambotmvc.annotation.BotController;
import com.tratsiak.telegrambotmvc.annotation.BotRequestMapping;
import com.tratsiak.telegrambotmvc.core.dispatcher.AbstractDispatcherRequests;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DefaultControllerDispatcherRequests extends AbstractDispatcherRequests {


    public DefaultControllerDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        super(pathValidator, context);
    }

    @Override
    public void init() {
        Map<String, Object> mapControllerBeans = context.getBeansWithAnnotation(BotController.class);


        for (String bean : mapControllerBeans.keySet()) {
            BotRequestMapping annatationBotRequestMapping = context.findAnnotationOnBean(bean, BotRequestMapping.class);

            String path = "";
            if (annatationBotRequestMapping != null) {
                path = annatationBotRequestMapping.path();
            }

            Object object = mapControllerBeans.get(bean);

            for (Method method : object.getClass().getDeclaredMethods()) {
                BotRequestMapping annotationOnMethod = method.getDeclaredAnnotation(BotRequestMapping.class);


                if (annotationOnMethod != null) {
                    String finalPath = path + annotationOnMethod.path();
                    put(finalPath, method, object);
                }
            }
        }

    }

}
