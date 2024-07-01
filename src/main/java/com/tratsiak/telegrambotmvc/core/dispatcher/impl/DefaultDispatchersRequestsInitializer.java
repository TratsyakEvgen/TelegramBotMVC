package com.tratsiak.telegrambotmvc.core.dispatcher.impl;


import com.tratsiak.telegrambotmvc.core.dispatcher.DispatcherRequests;
import com.tratsiak.telegrambotmvc.core.dispatcher.DispatchersRequestsInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;


public class DefaultDispatchersRequestsInitializer implements DispatchersRequestsInitializer {

    private final ApplicationContext context;
    private Map<String, DispatcherRequests> dispatcherRequestsMap;

    public DefaultDispatchersRequestsInitializer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    @EventListener(classes = ContextRefreshedEvent.class)
    public void init() {
        dispatcherRequestsMap = context.getBeansOfType(DispatcherRequests.class);
        dispatcherRequestsMap.forEach((string, dispatcherRequests) -> dispatcherRequests.init());

    }

    @Override
    public Map<String, DispatcherRequests> getDispatcherRequestsMap() {
        return dispatcherRequestsMap;
    }
}
