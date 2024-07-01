package com.tratsiak.telegrambotmvc.core.dispatcher.impl;


import com.tratsiak.telegrambotmvc.annotation.BotStaticResource;
import com.tratsiak.telegrambotmvc.annotation.BotViewStaticResource;
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
public class DefaultViewDispatcherRequests extends AbstractDispatcherRequests {

    public DefaultViewDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        super(pathValidator, context);
    }

    @Override
    public void init() {
        Map<String, Object> mapControllerBeans = context.getBeansWithAnnotation(BotViewStaticResource.class);

        for (String bean : mapControllerBeans.keySet()) {
            BotStaticResource annotationOnBean = context.findAnnotationOnBean(bean, BotStaticResource.class);

            String path = "";
            if (annotationOnBean != null) {
                path = annotationOnBean.path();
            }

            Object object = mapControllerBeans.get(bean);

            for (Method method : object.getClass().getDeclaredMethods()) {
                BotStaticResource annotationOnMethod = method.getDeclaredAnnotation(BotStaticResource.class);

                if (annotationOnMethod != null) {
                    String finalPath = path + annotationOnMethod.path();
                    put(finalPath, method, object);

                }
            }
        }
    }

}
