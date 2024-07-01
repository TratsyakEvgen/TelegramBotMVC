package com.tratsiak.telegrambotmvc.core.session.impl;

import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;

import java.util.Optional;


public class DefaultSessionInitializer implements SessionInitializer {
    @Override
    public Optional<Session> init(long id) {
        return Optional.of(new DefaultSession(id));
    }
}
