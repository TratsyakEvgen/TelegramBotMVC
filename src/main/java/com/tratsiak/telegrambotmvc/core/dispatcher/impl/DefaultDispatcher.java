package com.tratsiak.telegrambotmvc.core.dispatcher.impl;


import com.tratsiak.telegrambotmvc.annotation.BotEndpoint;
import com.tratsiak.telegrambotmvc.core.dispatcher.Dispatcher;
import com.tratsiak.telegrambotmvc.core.dispatcher.DispatcherException;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


@Getter
@Setter
@ToString
public class DefaultDispatcher implements Dispatcher {

    private final PathValidator pathValidator;

    private final List<Object> controllers;

    private final Map<String, MethodOfObject> methodsMap;


    public DefaultDispatcher(PathValidator pathValidator, List<Object> controllers) {
        this.pathValidator = pathValidator;
        this.controllers = controllers;
        this.methodsMap = new HashMap<>();
    }

    @PostConstruct
    @Override
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
    public Optional<View> execute(Session session) {

        String endpoint = session.getCurrentEndpoint();
        MethodOfObject methodOfObject = methodsMap.get(endpoint);
        Optional<MethodOfObject> optionalMethodOfObject = Optional.ofNullable(methodOfObject);

        try {
            View view = (View) optionalMethodOfObject.orElseThrow(
                    () -> new DispatcherException(String.format("Endpoint %s not found", endpoint))
            ).method.invoke(methodOfObject.object, session);
            return Optional.ofNullable(view);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DispatcherException("Can't invoke method for endpoint " + endpoint, e);
        }
    }


    @AllArgsConstructor
    private static class MethodOfObject {
        private Object object;
        private Method method;
    }

}
