package com.tratsiak.telegrambotmvc.core;


import com.tratsiak.telegrambotmvc.core.dispatcher.Dispatcher;
import com.tratsiak.telegrambotmvc.core.exeption.handler.ErrorViewer;
import com.tratsiak.telegrambotmvc.core.exeption.handler.MapperExceptionHandler;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;
import com.tratsiak.telegrambotmvc.core.session.SessionsProvider;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.executor.MethodExecutor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;


@Slf4j
public class BotMVC extends TelegramLongPollingBot {

    private final Dispatcher dispatcher;
    private final MethodExecutor methodExecutor;
    private final SessionsProvider sessionsProvider;
    private final MapperExceptionHandler mapperExceptionHandler;
    private final ErrorViewer errorViewer;
    private final String botName;
    private final SessionModifier sessionModifier;


    public BotMVC(String botToken,
                  Dispatcher dispatcher,
                  MethodExecutor methodExecutor,
                  String botName,
                  SessionsProvider botSession, MapperExceptionHandler mapperExceptionHandler, ErrorViewer errorViewer, SessionModifier sessionModifier) {
        super(botToken);
        this.dispatcher = dispatcher;
        this.methodExecutor = methodExecutor;
        this.botName = botName;
        this.sessionsProvider = botSession;
        this.mapperExceptionHandler = mapperExceptionHandler;
        this.errorViewer = errorViewer;
        this.sessionModifier = sessionModifier;
    }

    @Override
    public void onUpdateReceived(Update update) {
        new Thread(() -> sendAnswer(update)).start();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }


    private void sendAnswer(Update update) {
        try {
            Session session = sessionsProvider.getSession(update).orElseThrow();

            if (update.hasCallbackQuery()) {
                execute(new AnswerCallbackQuery(update.getCallbackQuery().getId()));
            }


            sessionModifier.modify(session);


            Optional<View> view;
            try {
                view = dispatcher.execute(session);
            } catch (Exception e) {
                log.warn("Error",e);
                view = mapperExceptionHandler.handle(e, session);
            }


            view.orElseGet(() -> errorViewer.getDefaultError(session))
                    .getMessages()
                    .forEach(message -> methodExecutor.execute(this, message, "execute"));


        } catch (Exception e) {
            log.error("Core error", e);
        }
    }
}
