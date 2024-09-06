package com.tratsiak.telegrambotmvc.reflection;

public interface MethodExecutor {
    void execute(Object object, Object arg, String methodName);
}
