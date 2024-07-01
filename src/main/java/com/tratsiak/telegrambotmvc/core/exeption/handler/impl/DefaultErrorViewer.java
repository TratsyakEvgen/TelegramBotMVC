package com.tratsiak.telegrambotmvc.core.exeption.handler.impl;


import com.tratsiak.telegrambotmvc.core.exeption.handler.ErrorViewer;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.BotView;
import com.tratsiak.telegrambotmvc.core.view.View;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public class DefaultErrorViewer implements ErrorViewer {
    @Override
    public View getDefaultError(Session session) {
        return new BotView(SendMessage.builder().chatId(session.getId()).text("Error!!!").build());
    }
}
