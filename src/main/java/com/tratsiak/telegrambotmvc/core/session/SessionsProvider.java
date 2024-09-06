package com.tratsiak.telegrambotmvc.core.session;


import com.tratsiak.telegrambotmvc.core.parser.Request;

public interface SessionsProvider {
    Session getSession(Request request);
}
