package com.tratsiak.telegrambotmvc.core;


import com.tratsiak.telegrambotmvc.core.dispatcher.DispatcherRequests;
import com.tratsiak.telegrambotmvc.core.dispatcher.DispatchersRequestsInitializer;
import com.tratsiak.telegrambotmvc.core.exeption.handler.ErrorViewer;
import com.tratsiak.telegrambotmvc.core.exeption.handler.MapperExceptionHandler;
import com.tratsiak.telegrambotmvc.core.path.PathParser;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.session.BotSessions;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.executor.MethodExecutor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Slf4j
public class BotMVC extends TelegramLongPollingBot {

    private final DispatchersRequestsInitializer dispatchersRequestsInitializer;
    private final PathValidator pathValidator;
    private final PathParser pathParser;
    private final MethodExecutor methodExecutor;
    private final BotSessions botSessions;
    private final MapperExceptionHandler mapperExceptionHandler;
    private final ErrorViewer errorViewer;
    private final String botName;
    private final SessionModifier sessionModifier;


    public BotMVC(String botToken,
                  DispatchersRequestsInitializer dispatchersRequestsInitializer,
                  PathValidator pathValidator,
                  PathParser pathParser,
                  MethodExecutor methodExecutor,
                  String botName,
                  BotSessions botSession, MapperExceptionHandler mapperExceptionHandler, ErrorViewer errorViewer, SessionModifier sessionModifier) {
        super(botToken);
        this.dispatchersRequestsInitializer = dispatchersRequestsInitializer;
        this.pathValidator = pathValidator;
        this.pathParser = pathParser;
        this.methodExecutor = methodExecutor;
        this.botName = botName;
        this.botSessions = botSession;
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

        Session session;
        try {
            session = botSessions.getSession(update);
        } catch (Exception e) {
            log.error("Can't get session", e);
            return;
        }


        Optional<View> optionalView = Optional.empty();
        try {

            if (update.hasCallbackQuery()) {
                execute(new AnswerCallbackQuery(update.getCallbackQuery().getId()));
            }

            String path = session.getCurrentCommand();

            pathValidator.isValidPathWithParams(path);
            session.setCurrentCommand(pathParser.getPath(path));
            session.setParameters(pathParser.getParam(path));

            sessionModifier.modify(session);

            Collection<DispatcherRequests> mappers = dispatchersRequestsInitializer.getDispatcherRequestsMap().values();
            for (DispatcherRequests dispatcherRequests : mappers) {
                optionalView = dispatcherRequests.executeMethod(session);
                if (optionalView.isPresent()) {
                    break;

                }
            }

            if (optionalView.isEmpty()) {
                log.error(String.format("Endpoint '%s' not found", path));
            }


        } catch (Exception e) {
            optionalView = mapperExceptionHandler.handle(e, session);
            if (optionalView.isEmpty()) {
                log.error("Core error", e);
            }
        }

        try {
            List<PartialBotApiMethod<?>> messages = optionalView
                    .orElseGet(() -> errorViewer.getDefaultError(session))
                    .getMessages();

            for (PartialBotApiMethod<?> sendingMessage : messages) {
                methodExecutor.executeVoidMethodWithParameter(this, sendingMessage, "execute");
            }
        } catch (Exception e) {
            log.error("Core error", e);
        } finally {
            session.setPastCommand(session.getCurrentCommand());
            session.setTextMessage(null);
            session.setCurrentCommand(null);
            session.setParameters(null);
        }


    }
}
