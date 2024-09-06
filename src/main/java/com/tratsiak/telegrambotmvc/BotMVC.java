package com.tratsiak.telegrambotmvc;


import com.tratsiak.telegrambotmvc.core.dispatcher.EndpointDispatcher;
import com.tratsiak.telegrambotmvc.core.exeption.handler.DispatcherExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.DispatcherExceptionHandlerException;
import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserProvider;
import com.tratsiak.telegrambotmvc.core.parser.exception.UpdateParserException;
import com.tratsiak.telegrambotmvc.core.parser.exception.UpdateParserProviderException;
import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.session.SessionException;
import com.tratsiak.telegrambotmvc.core.session.SessionsProvider;
import com.tratsiak.telegrambotmvc.core.view.View;
import com.tratsiak.telegrambotmvc.reflection.MethodExecutor;
import com.tratsiak.telegrambotmvc.reflection.MethodExecutorException;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Slf4j
public class BotMVC extends TelegramLongPollingBot {
    private final UpdateParserProvider updateParserProvider;
    private final EndpointDispatcher endpointDispatcher;
    private final MethodExecutor methodExecutor;
    private final SessionsProvider sessionsProvider;
    private final DispatcherExceptionHandler dispatcherExceptionHandler;
    private final String botName;


    public BotMVC(String botToken, UpdateParserProvider updateParserProvider,
                  EndpointDispatcher endpointDispatcher,
                  MethodExecutor methodExecutor,
                  String botName,
                  SessionsProvider botSession,
                  DispatcherExceptionHandler dispatcherExceptionHandler) {
        super(botToken);
        this.updateParserProvider = updateParserProvider;
        this.endpointDispatcher = endpointDispatcher;
        this.methodExecutor = methodExecutor;
        this.botName = botName;
        this.sessionsProvider = botSession;
        this.dispatcherExceptionHandler = dispatcherExceptionHandler;
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

        answerIfUpdateHasCallback(update);

        View view;
        Session session = null;
        try {

            UpdateParser updateParser = updateParserProvider.getUpdateParser(update);
            Request request = updateParser.parse(update);
            session = sessionsProvider.getSession(request);
            view = endpointDispatcher.invoke(session);
            sendView(view);

        } catch (UpdateParserException | UpdateParserProviderException e) {
            log.error("Core error! Cannot parse update", e);
        } catch (SessionException e) {
            log.error("Cannot get session", e);
        } catch (Exception e) {
            log.warn("Core error", e);
            handleException(e, session);
        }
    }

    private void handleException(Exception exception, Session session) {
        try {
            dispatcherExceptionHandler.handle(exception, session).ifPresent(this::sendView);
        } catch (DispatcherExceptionHandlerException e) {
            log.error("Cannot handle exception", e);
        }

    }

    private void sendView(View view) {
        List<PartialBotApiMethod<?>> messages = view.getMessages();
        try {
            messages.forEach(message -> methodExecutor.execute(this, message, "execute"));
        } catch (MethodExecutorException e) {
            log.error("Cannot send view wits messages: " + messages, e);
        }

    }

    private void answerIfUpdateHasCallback(Update update) {
        if (update.hasCallbackQuery()) {
            try {
                execute(new AnswerCallbackQuery(update.getCallbackQuery().getId()));
            } catch (TelegramApiException e) {
                log.error("Cannot answer the callback", e);
            }
        }
    }
}
