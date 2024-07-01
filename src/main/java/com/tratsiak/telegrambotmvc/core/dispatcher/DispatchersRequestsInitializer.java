package com.tratsiak.telegrambotmvc.core.dispatcher;


import java.util.Map;

public interface DispatchersRequestsInitializer {
    void init();

    Map<String, DispatcherRequests> getDispatcherRequestsMap();
}
