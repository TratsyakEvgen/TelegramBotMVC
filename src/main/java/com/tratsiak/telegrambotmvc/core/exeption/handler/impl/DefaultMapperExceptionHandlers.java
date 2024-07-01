package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;

import com.tratsiak.telegrambotmvc.core.exeption.handler.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.MapperExceptionHandler;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class DefaultMapperExceptionHandlers implements MapperExceptionHandler {

    private final ApplicationContext context;
    private final Map<Class<?>, ExceptionHandler<? extends Exception>> handlerMap;


    public DefaultMapperExceptionHandlers(ApplicationContext context) {
        this.context = context;
        this.handlerMap = new HashMap<>();
    }

    @Override
    @EventListener(classes = ContextRefreshedEvent.class)
    public void init() {
        Map<String, ExceptionHandler> beansOfType = context.getBeansOfType(ExceptionHandler.class);
        beansOfType.forEach((string, exceptionHandler) -> {
                    Type[] genericInterfaces = exceptionHandler.getClass().getGenericInterfaces();
                    for (Type genericInterface : genericInterfaces) {
                        if (genericInterface instanceof ParameterizedType) {
                            Type genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                            handlerMap.put((Class<?>) genericTypes, exceptionHandler);
                        }
                    }
                }
        );
    }

    @Override
    public Optional<View> handle(Exception e, Session session) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }


        Throwable finalCause = cause;
        return handlerMap.entrySet()
                .stream()
                .filter((entry -> entry.getKey().isInstance(finalCause.getClass())))
                .findFirst()
                .map(classExceptionHandlerEntry -> classExceptionHandlerEntry.getValue().handle(e, session));
    }
}
