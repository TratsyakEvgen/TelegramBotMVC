package com.tratsiak.telegrambotmvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratsiak.telegrambotmvc.BotMVC;
import com.tratsiak.telegrambotmvc.annotation.BotEndpoint;
import com.tratsiak.telegrambotmvc.annotation.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.dispatcher.EndpointDispatcher;
import com.tratsiak.telegrambotmvc.core.dispatcher.impl.EndpointDispatcherImpl;
import com.tratsiak.telegrambotmvc.core.exeption.handler.DispatcherExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.impl.DefaultExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.impl.DispatcherExceptionHandlersImpl;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserProvider;
import com.tratsiak.telegrambotmvc.core.parser.impl.*;
import com.tratsiak.telegrambotmvc.core.path.PathParser;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.path.impl.DefaultPathParser;
import com.tratsiak.telegrambotmvc.core.path.impl.DefaultPathValidator;
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;
import com.tratsiak.telegrambotmvc.core.session.SessionsProvider;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionModifier;
import com.tratsiak.telegrambotmvc.core.session.impl.SessionsProviderImpl;
import com.tratsiak.telegrambotmvc.reflection.MethodExecutor;
import com.tratsiak.telegrambotmvc.reflection.MethodExecutorImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Configuration
public class TelegramBotMVCAutoConfiguration {

    @Bean
    public DefaultExceptionHandler defaultExceptionHandler() {
        return new DefaultExceptionHandler();
    }

    @Bean
    public UpdateParser updateBotCommandParser() {
        return new UpdateBotCommandParser();
    }

    @Bean
    public UpdateParser updateCallbackQueryParser() {
        return new UpdateCallbackQueryParser();
    }

    @Bean
    public UpdateParser updateUserMessageParser() {
        return new UpdateUserMessageParser();
    }

    @Bean
    public UpdateParser updateWebAppParser(ObjectMapper objectMapper) {
        return new UpdateWebAppParser(objectMapper);
    }

    @Bean
    public UpdateParserProvider updateParserProvider(List<UpdateParser> updateParsers) {
        return new UpdateParserProviderImpl(updateParsers);
    }

    @Bean
    public EndpointDispatcher controllerDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        List<Object> controllers = context.getBeansWithAnnotation(BotEndpoint.class).values().stream().toList();
        return new EndpointDispatcherImpl(pathValidator, controllers);
    }

    @Bean
    public DispatcherExceptionHandler mapperExceptionHandler(ApplicationContext context) {
        List<Object> handlers = context.getBeansWithAnnotation(ExceptionHandler.class).values().stream().toList();
        return new DispatcherExceptionHandlersImpl(handlers);
    }

    @Bean
    public PathParser pathParser() {
        return new DefaultPathParser();
    }

    @Bean
    public PathValidator pathValidator() {
        return new DefaultPathValidator();
    }

    @Bean
    @ConditionalOnMissingBean(SessionModifier.class)
    public SessionModifier sessionModifier() {
        return new DefaultSessionModifier();
    }

    @Bean
    @ConditionalOnMissingBean(SessionInitializer.class)
    public SessionInitializer sessionInitializer() {
        return new DefaultSessionInitializer();
    }

    @Bean
    public SessionsProvider sessionProvider(SessionInitializer sessionInitializer,
                                            PathValidator pathValidator,
                                            PathParser pathParser,
                                            SessionModifier sessionModifier) {
        return new SessionsProviderImpl(sessionModifier, sessionInitializer, pathValidator, pathParser);
    }


    @Bean
    @ConditionalOnProperty(name = {"botToken", "botName"})
    public LongPollingBot longPollingBot(UpdateParserProvider updateParserProvider,
                                         @Value("${botToken}") String botToken,
                                         EndpointDispatcher endpointDispatcher,
                                         MethodExecutor methodExecutor,
                                         @Value("${botName}") String botName,
                                         SessionsProvider botSession,
                                         DispatcherExceptionHandler dispatcherExceptionHandler) {
        return new BotMVC(botToken, updateParserProvider, endpointDispatcher, methodExecutor, botName,
                botSession, dispatcherExceptionHandler);
    }

    @Bean
    @ConditionalOnBean(LongPollingBot.class)
    public BotSession botSession(LongPollingBot bot) throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
    }

    @Bean
    public MethodExecutor methodExecutor() {
        return new MethodExecutorImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
