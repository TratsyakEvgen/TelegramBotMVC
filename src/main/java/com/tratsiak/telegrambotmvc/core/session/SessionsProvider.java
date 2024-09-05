package com.tratsiak.telegrambotmvc.core.session;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

public interface SessionsProvider {
    Optional<Session> getSession(Update update);
}
