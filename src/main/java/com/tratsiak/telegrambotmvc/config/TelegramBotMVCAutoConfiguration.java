package com.tratsiak.telegrambotmvc.config;

import com.tratsiak.telegrambotmvc.components.ComponentInlineKeyboardButton;
import com.tratsiak.telegrambotmvc.components.ComponentInlineKeyboardMarkup;
import com.tratsiak.telegrambotmvc.components.ComponentSendAudio;
import com.tratsiak.telegrambotmvc.components.ComponentSendMessage;
import com.tratsiak.telegrambotmvc.components.impl.DefaultComponentInlineKeyboardButton;
import com.tratsiak.telegrambotmvc.components.impl.DefaultComponentInlineKeyboardMarkup;
import com.tratsiak.telegrambotmvc.components.impl.DefaultComponentSendAudio;
import com.tratsiak.telegrambotmvc.components.impl.DefaultComponentSendMessage;
import com.tratsiak.telegrambotmvc.core.BotMVC;
import com.tratsiak.telegrambotmvc.core.dispatcher.DispatcherRequests;
import com.tratsiak.telegrambotmvc.core.dispatcher.DispatchersRequestsInitializer;
import com.tratsiak.telegrambotmvc.core.dispatcher.impl.DefaultControllerDispatcherRequests;
import com.tratsiak.telegrambotmvc.core.dispatcher.impl.DefaultDispatchersRequestsInitializer;
import com.tratsiak.telegrambotmvc.core.dispatcher.impl.DefaultViewDispatcherRequests;
import com.tratsiak.telegrambotmvc.core.exeption.handler.ErrorViewer;
import com.tratsiak.telegrambotmvc.core.exeption.handler.ExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.MapperExceptionHandler;
import com.tratsiak.telegrambotmvc.core.exeption.handler.impl.DefaultErrorViewer;
import com.tratsiak.telegrambotmvc.core.exeption.handler.impl.DefaultMapperExceptionHandlers;
import com.tratsiak.telegrambotmvc.core.exeption.handler.impl.DefaultResponseExceptionHandler;
import com.tratsiak.telegrambotmvc.core.path.PathParser;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;
import com.tratsiak.telegrambotmvc.core.path.impl.DefaultPathParser;
import com.tratsiak.telegrambotmvc.core.path.impl.DefaultPathValidator;
import com.tratsiak.telegrambotmvc.core.session.BotSessions;
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultBotSessions;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionModifier;
import com.tratsiak.telegrambotmvc.exception.ResponseException;
import com.tratsiak.telegrambotmvc.executor.DefaultMethodExecutor;
import com.tratsiak.telegrambotmvc.executor.MethodExecutor;
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

@Configuration
public class TelegramBotMVCAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ComponentSendMessage.class)
    public ComponentSendMessage componentSendMessage() {
        return new DefaultComponentSendMessage();
    }

    @Bean
    @ConditionalOnMissingBean(ComponentInlineKeyboardButton.class)
    public ComponentInlineKeyboardButton componentInlineKeyboardButton() {
        return new DefaultComponentInlineKeyboardButton();
    }

    @Bean
    @ConditionalOnMissingBean(ComponentInlineKeyboardMarkup.class)
    public ComponentInlineKeyboardMarkup componentInlineKeyboardMarkup() {
        return new DefaultComponentInlineKeyboardMarkup();
    }

    @Bean
    @ConditionalOnMissingBean(ComponentSendAudio.class)
    public ComponentSendAudio componentSendAudio() {
        return new DefaultComponentSendAudio();
    }

    @Bean
    public DispatcherRequests controllerDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        return new DefaultControllerDispatcherRequests(pathValidator, context);
    }

    @Bean
    public DispatcherRequests viewDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        return new DefaultViewDispatcherRequests(pathValidator, context);
    }

    @Bean
    @ConditionalOnMissingBean(DispatchersRequestsInitializer.class)
    public DispatchersRequestsInitializer dispatchersRequestsInitializer(ApplicationContext context) {
        return new DefaultDispatchersRequestsInitializer(context);
    }

    @Bean
    @ConditionalOnMissingBean(ErrorViewer.class)
    public ErrorViewer errorViewer() {
        return new DefaultErrorViewer();
    }

    @Bean
    public ExceptionHandler<ResponseException> responseExceptionExceptionHandler() {
        return new DefaultResponseExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean(MapperExceptionHandler.class)
    public MapperExceptionHandler mapperExceptionHandler(ApplicationContext context) {
        return new DefaultMapperExceptionHandlers(context);
    }

    @Bean
    @ConditionalOnMissingBean(PathParser.class)
    public PathParser pathParser() {
        return new DefaultPathParser();
    }

    @Bean
    @ConditionalOnMissingBean(PathValidator.class)
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
    @ConditionalOnMissingBean(BotSessions.class)
    public BotSessions botSessions(SessionInitializer sessionInitializer) {
        return new DefaultBotSessions(sessionInitializer);
    }


    @Bean
    @ConditionalOnProperty(name = {"botToken", "botName"})
    @ConditionalOnMissingBean(LongPollingBot.class)
    public LongPollingBot longPollingBot(@Value("${botToken}") String botToken,
                                         DispatchersRequestsInitializer dispatchersRequestsInitializer,
                                         PathValidator pathValidator,
                                         PathParser pathParser,
                                         MethodExecutor methodExecutor,
                                         @Value("${botName}") String botName,
                                         BotSessions botSession,
                                         MapperExceptionHandler mapperExceptionHandler,
                                         ErrorViewer errorViewer,
                                         SessionModifier sessionModifier) {
        return new BotMVC(botToken, dispatchersRequestsInitializer, pathValidator, pathParser, methodExecutor, botName,
                botSession, mapperExceptionHandler, errorViewer, sessionModifier);
    }

    @Bean
    @ConditionalOnBean(LongPollingBot.class)
    public BotSession botSession(LongPollingBot bot) throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
    }

    @Bean
    @ConditionalOnMissingBean(MethodExecutor.class)
    public MethodExecutor methodExecutor() {
        return new DefaultMethodExecutor();
    }


}
