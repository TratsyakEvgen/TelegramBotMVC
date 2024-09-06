package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;

import com.tratsiak.telegrambotmvc.annotation.ClassException;
import com.tratsiak.telegrambotmvc.core.exeption.handler.DispatcherExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.DispatcherExceptionHandlerException;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.reflection.MethodOfObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@RequiredArgsConstructor
public class DispatcherExceptionHandlersImpl implements DispatcherExceptionHandler {

    private final List<Object> handlers;
    private final Map<Class<?>, MethodOfObject> classMethodOfObjectMap = new HashMap<>();

    @PostConstruct
    public void init() {
        handlers.forEach(bean ->
                Arrays.stream(bean.getClass().getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(ClassException.class))
                        .forEach(method -> {
                            ClassException annotationClassException = method.getDeclaredAnnotation(ClassException.class);
                            Class<? extends Exception> exceptionClass = annotationClassException.exception();

                            method.setAccessible(true);
                            classMethodOfObjectMap.put(exceptionClass, new MethodOfObject(bean, method));
                        })
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<View> handle(Exception exception, Session session) {
        Throwable finalCause = getCause(exception);

        Class<?> exceptionClass = finalCause.getClass();
        MethodOfObject methodOfObject = classMethodOfObjectMap.get(exceptionClass);

        try {
            if (methodOfObject != null) {
                return (Optional<View>) methodOfObject.method().invoke(methodOfObject.object(), finalCause, session);
            }
            return Optional.empty();

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DispatcherExceptionHandlerException("Can't invoke method for exception " + exceptionClass, e);
        }
    }

    private Throwable getCause(Exception e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

}
