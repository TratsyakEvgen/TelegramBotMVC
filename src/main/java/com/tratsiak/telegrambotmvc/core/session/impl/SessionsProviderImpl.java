package com.tratsiak.telegrambotmvc.core.session.impl;


import com.tratsiak.telegrambotmvc.core.path.PathParser;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionException;
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.SessionsProvider;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.Request;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.UpdateParserProvider;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionsProviderImpl implements SessionsProvider {
    private final UpdateParserProvider updateParserProvider;
    private final Map<Long, Session> sessions;
    private final SessionInitializer sessionInitializer;
    private final PathValidator pathValidator;
    private final PathParser pathParser;

    public SessionsProviderImpl(UpdateParserProvider updateParserProvider, SessionInitializer sessionInitializer, PathValidator pathValidator, PathParser pathParser) {
        this.updateParserProvider = updateParserProvider;
        this.sessionInitializer = sessionInitializer;
        this.pathValidator = pathValidator;
        this.pathParser = pathParser;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Session> getSession(Update update) {

        UpdateParser updateParser = updateParserProvider.getUpdateParser(update);
        Request request = updateParser.parse(update);

        long id = request.getId();

        Session session = Optional.ofNullable(sessions.get(id))
                .or(() -> sessionInitializer.init(id))
                .orElseThrow(() -> new SessionException("Session mustn't be null"));

        session.setPreviousEndpoint(session.getCurrentEndpoint());

        String path = Optional.ofNullable(request.getPath())
                .or(() -> Optional.ofNullable(session.getNextEndpoint()))
                .orElseThrow(() -> new SessionException("Endpoint is null"));

        session.setMessage(request.getBody());


        pathValidator.isValidPathWithParams(path);
        session.setCurrentEndpoint(pathParser.getPath(path));
        session.setParameters(pathParser.getParam(path));

        session.setNextEndpoint(null);

        sessions.put(id, session);
        return Optional.of(session);
    }

}
