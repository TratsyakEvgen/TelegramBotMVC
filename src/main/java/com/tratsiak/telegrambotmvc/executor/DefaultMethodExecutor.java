package com.tratsiak.telegrambotmvc.executor;


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DefaultMethodExecutor implements MethodExecutor {
    @Override
    public void execute(Object object, Object param, String paramName) {
        Class<?> paramClass = param.getClass();
        try {
            Arrays.stream(object.getClass().getMethods())
                    .filter(method -> method.getName().equals(paramName))
                    .filter(method -> Arrays.stream(method.getParameterTypes())
                            .allMatch(p -> p.equals(paramClass) | p.isAssignableFrom(paramClass)))
                    .findFirst()
                    .orElseThrow(() -> new MethodExecutorException("Method execute(" + paramClass + ") not found"))
                    .invoke(object, param);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodExecutorException(
                    String.format("Can't invoke method %s with parameter %s", object.getClass().getName(), param)
            );
        }
    }
}
