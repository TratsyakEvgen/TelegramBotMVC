package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;


import com.tratsiak.telegrambotmvc.annotation.ClassException;
import com.tratsiak.telegrambotmvc.annotation.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.BotView;
import com.tratsiak.telegrambotmvc.core.view.ResponseException;
import com.tratsiak.telegrambotmvc.core.view.View;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@ExceptionHandler
public class DefaultExceptionHandler {

    @ClassException(exception = ResponseException.class)
    public Optional<View> handleResponseException(Exception exception, Session session) {
        ResponseException responseException = (ResponseException) exception;
        return Optional.ofNullable(responseException.getView())
                .or(() -> Optional.of(new BotView(SendMessage.builder()
                        .chatId(session.getId())
                        .text(exception.getMessage())
                        .build())));
    }
}
