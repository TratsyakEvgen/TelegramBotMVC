package com.tratsiak.telegrambotmvc.core.dispatcher.impl;


import com.tratsiak.telegrambotmvc.annotation.BotEndpoint;
import com.tratsiak.telegrambotmvc.core.dispatcher.EndpointDispatcher;
import com.tratsiak.telegrambotmvc.core.dispatcher.EndpointDispatcherException;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.reflection.MethodOfObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


@RequiredArgsConstructor
public class EndpointDispatcherImpl implements EndpointDispatcher {

    private final PathValidator pathValidator;

    private final List<Object> controllers;
    private final Map<String, MethodOfObject> methodsMap = new HashMap<>();


    @PostConstruct
    public void init() {
        controllers.forEach(bean -> {
            Class<?> beanClass = bean.getClass();

            BotEndpoint annatationBotEndpoint = beanClass.getAnnotation(BotEndpoint.class);
            String path = annatationBotEndpoint.path();

            Arrays.stream(beanClass.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(BotEndpoint.class))
                    .forEach(method -> {

                        BotEndpoint annotationOnMethod = method.getDeclaredAnnotation(BotEndpoint.class);
                        String finalPath = path + annotationOnMethod.path();

                        pathValidator.isValidPath(finalPath);

                        method.setAccessible(true);
                        methodsMap.put(finalPath, new MethodOfObject(bean, method));

                    });
        });
    }


    @Override
    public View invoke(Session session) {

        String endpoint = session.getCurrentEndpoint();
        MethodOfObject methodOfObject = methodsMap.get(endpoint);

        try {
            return (View) Optional.ofNullable(methodOfObject)
                    .orElseThrow(() -> new EndpointDispatcherException(String.format("Endpoint %s not found", endpoint)))
                    .method()
                    .invoke(methodOfObject.object(), session);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EndpointDispatcherException("Can't invoke method for endpoint " + endpoint, e);
        }
    }


}
