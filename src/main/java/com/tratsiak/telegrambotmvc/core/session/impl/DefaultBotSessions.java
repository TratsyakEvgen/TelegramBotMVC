package com.tratsiak.telegrambotmvc.core.session.impl;


import com.tratsiak.telegrambotmvc.core.session.BotSessions;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionException;
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@EqualsAndHashCode
@ToString
public class DefaultBotSessions implements BotSessions {

    private final Map<Long, Session> sessions;
    private final SessionInitializer sessionInitializer;

    public DefaultBotSessions(SessionInitializer sessionInitializer) {
        this.sessionInitializer = sessionInitializer;
        this.sessions = new ConcurrentHashMap<>();
    }

    @Override
    public Session getSession(Update update) {

        Session session;
        long userId;

        if (update.hasCallbackQuery()) {

            CallbackQuery query = update.getCallbackQuery();
            userId = query.getFrom().getId();

            session = getOrCreateAndModifySession(userId);

            session.setCurrentCommand(query.getData());
            session.setNextCommand(null);

        } else {
            Message message;

            message = update.getMessage();
            userId = message.getFrom().getId();

            session = getOrCreateAndModifySession(userId);

            session.setTextMessage(message.getText());
            session.setCurrentCommand(session.getNextCommand());
            session.setNextCommand(null);

            if (message.isCommand()) {
                session.setCurrentCommand("/start");
            }

        }

        sessions.put(session.getId(), session);
        return session;
    }


    private Session getOrCreateAndModifySession(long id) {
        return Optional.ofNullable(sessions.get(id))
                .or(() -> sessionInitializer.init(id))
                .orElseThrow(() -> new SessionException("Session should not be null"));
    }
}
