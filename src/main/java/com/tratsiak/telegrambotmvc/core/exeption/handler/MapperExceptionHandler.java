package com.tratsiak.telegrambotmvc.core.exeption.handler;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;

import java.util.Optional;

public interface MapperExceptionHandler {
    void init();

    Optional<View> handle(Exception e, Session session);
}
