package com.tratsiak.telegrambotmvc.core.session.impl;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;


public class DefaultSessionModifier implements SessionModifier {
    @Override
    public Session modify(Session session) {
        return session;
    }
}
