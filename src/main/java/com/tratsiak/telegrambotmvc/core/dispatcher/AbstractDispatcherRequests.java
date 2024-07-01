package com.tratsiak.telegrambotmvc.core.dispatcher;

import com.tratsiak.telegrambotmvc.core.path.NotValidPathException;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Getter
@Setter
public abstract class AbstractDispatcherRequests implements DispatcherRequests {

    protected final ApplicationContext context;

    protected final Map<String, MethodOfObject> methodsMap;

    private final PathValidator pathValidator;

    public AbstractDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        this.context = context;
        this.pathValidator = pathValidator;
        this.methodsMap = new HashMap<>();
    }

    @Override
    public Optional<View> executeMethod(Session session) {
        MethodOfObject methodOfObject = methodsMap.get(session.getCurrentCommand());

        if (methodOfObject == null) {
            return Optional.empty();
        }
        try {
            View view = (View) methodOfObject.method.invoke(methodOfObject.object, session);
            return Optional.ofNullable(view);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ExecuteMethodDispatcherRequestsException("Can't invoke method", e);
        }
    }

    @Override
    @EventListener(classes = ContextRefreshedEvent.class)
    public abstract void init();

    protected void put(String finalPath, Method method, Object object) {
        try {
            pathValidator.isValidPath(finalPath);
        } catch (NotValidPathException e) {
            throw new InitializationDispatcherRequestsException("Not valid path", e);
        }
        method.setAccessible(true);
        methodsMap.put(finalPath, new MethodOfObject(object, method));
    }

    @AllArgsConstructor
    protected static class MethodOfObject {
        private Object object;
        private Method method;
    }
}
