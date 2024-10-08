package com.tratsiak.telegrambotmvc.core.session.impl;

import com.tratsiak.telegrambotmvc.core.session.Session;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DefaultSession implements Session {

    private final long id;
    private String message;
    private String previousEndpoint;
    private String currentEndpoint;
    private String nextEndpoint;
    private Map<String, String> parameters;
    private Map<String, Object> entities;

    public DefaultSession(long id) {
        this.id = id;
        this.entities = new HashMap<>();
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }

    @Override
    public Object getEntity(String name) {
        return entities.get(name);
    }

    @Override
    public void setEntity(String name, Object object) {
        entities.put(name, object);
    }


}
