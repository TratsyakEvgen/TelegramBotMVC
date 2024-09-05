package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;

import com.tratsiak.telegrambotmvc.core.exeption.handler.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.MapperExceptionHandler;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class DefaultMapperExceptionHandlers implements MapperExceptionHandler {

    private final ApplicationContext context;
    private final Map<Class<?>, ExceptionHandler<? super Exception>> handlerMap;


    public DefaultMapperExceptionHandlers(ApplicationContext context) {
        this.context = context;
        this.handlerMap = new HashMap<>();
    }

    @Override
    @PostConstruct
    public void init() {
        context.getBeansOfType(ExceptionHandler.class)
                .values()
                .forEach(exceptionHandler -> {
                            Arrays.stream(exceptionHandler.getClass().getGenericInterfaces())
                                    .filter(genericInterface -> genericInterface instanceof ParameterizedType)
                                    .forEach(genericInterface -> {
                                        Type genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments()[0];
                                        handlerMap.put((Class<?>) genericTypes, exceptionHandler);
                                    });
                        }
                );
    }

    @Override
    public Optional<View> handle(Exception e, Session session) {
        return Optional.ofNullable(handlerMap.get(e.getClass()))
                .stream()
                .map(exceptionHandler -> exceptionHandler.handle(e, session))
                .findFirst();
    }

}
