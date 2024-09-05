package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;


import com.tratsiak.telegrambotmvc.core.exeption.handler.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.BotView;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.exception.ResponseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;


public class DefaultResponseExceptionHandler implements ExceptionHandler<ResponseException> {
    @Override
    public View handle(ResponseException exception, Session session) {
        return Optional.ofNullable(exception.getView())
                .orElseGet(() -> new BotView(SendMessage.builder()
                        .chatId(session.getId())
                        .text(exception.getMessage())
                        .build()));
    }
}
