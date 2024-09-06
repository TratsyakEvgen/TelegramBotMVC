package com.tratsiak.telegrambotmvc.core.session.impl;


import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.path.PathParser;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsProviderImpl implements SessionsProvider {

    private final Map<Long, Session> sessions;
    private final SessionModifier sessionModifier;
    private final SessionInitializer sessionInitializer;
    private final PathValidator pathValidator;
    private final PathParser pathParser;

    public SessionsProviderImpl(SessionModifier sessionModifier, SessionInitializer sessionInitializer, PathValidator pathValidator, PathParser pathParser) {
        this.sessionModifier = sessionModifier;
        this.sessionInitializer = sessionInitializer;
        this.pathValidator = pathValidator;
        this.pathParser = pathParser;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public Session getSession(Request request) {

        long id = request.getId();

        Session session = Optional.ofNullable(sessions.get(id))
                .or(() -> sessionInitializer.init(id))
                .orElseThrow(() -> new SessionException("Session mustn't be null"));

        session.setCurrentEndpoint(null);
        session.setPreviousEndpoint(session.getCurrentEndpoint());
        session.setMessage(request.getBody());

        Optional.ofNullable(request.getPath())
                .or(() -> Optional.ofNullable(session.getNextEndpoint()))
                .ifPresent(path -> {
                    pathValidator.isValidPathWithParams(path);
                    session.setCurrentEndpoint(pathParser.getPath(path));
                    session.setParameters(pathParser.getParam(path));
                });

        session.setNextEndpoint(null);

        Session modifySession = Optional.ofNullable(sessionModifier.modify(session))
                .orElseThrow(() -> new SessionException("Session after modify is null"));

        sessions.put(id, modifySession);
        return session;
    }

}
