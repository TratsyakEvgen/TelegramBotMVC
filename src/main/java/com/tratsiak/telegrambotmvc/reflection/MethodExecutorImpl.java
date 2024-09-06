package com.tratsiak.telegrambotmvc.reflection;


import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class MethodExecutorImpl implements MethodExecutor {
    @Override
    public void execute(Object object, Object arg, String methodName) {
        Class<?> paramClass = arg.getClass();
        try {
            Arrays.stream(object.getClass().getMethods())
                    .filter(method -> method.getName().equals(methodName))
                    .filter(method -> Arrays.stream(method.getParameterTypes())
                            .allMatch(p -> p.equals(paramClass) | p.isAssignableFrom(paramClass)))
                    .findFirst()
                    .orElseThrow(() -> new MethodExecutorException("Method execute(" + paramClass + ") not found"))
                    .invoke(object, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MethodExecutorException(
                    String.format("Can't invoke method %s with parameter %s", object.getClass().getName(), arg)
            );
        }
    }
}
