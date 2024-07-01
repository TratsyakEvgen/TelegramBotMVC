package com.tratsiak.telegrambotmvc.core.exeption.handler;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;

public interface ExceptionHandler<T extends Exception> {
    View handle(Exception exception, Session session);
}
