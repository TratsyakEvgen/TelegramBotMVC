package com.tratsiak.telegrambotmvc.core.session;

import java.util.Map;


public interface Session {


    long getId();

    String getPreviousEndpoint();

    void setPreviousEndpoint(String pastCommand);

    String getCurrentEndpoint();

    void setCurrentEndpoint(String currentCommand);

    String getNextEndpoint();

    void setNextEndpoint(String nextCommand);

    Map<String, String> getParameters();

    String getParameter(String name);

    void setParameters(Map<String, String> parameters);

    Map<String, Object> getEntities();

    Object getEntity(String name);

    void setEntity(String name, Object object);

    void setEntities(Map<String, Object> entities);

    String getMessage();

    void setMessage(String textMessage);
}
