package com.tratsiak.telegrambotmvc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratsiak.telegrambotmvc.annotation.BotEndpoint;
import com.tratsiak.telegrambotmvc.core.BotMVC;
import com.tratsiak.telegrambotmvc.core.dispatcher.Dispatcher;
import com.tratsiak.telegrambotmvc.core.dispatcher.impl.DefaultDispatcher;
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
import com.tratsiak.telegrambotmvc.core.session.SessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.SessionModifier;
import com.tratsiak.telegrambotmvc.core.session.SessionsProvider;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionInitializer;
import com.tratsiak.telegrambotmvc.core.session.impl.DefaultSessionModifier;
import com.tratsiak.telegrambotmvc.core.session.impl.SessionsProviderImpl;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.UpdateParserProvider;
import com.tratsiak.telegrambotmvc.core.session.impl.parser.impl.*;
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

import java.util.List;

@Configuration
public class TelegramBotMVCAutoConfiguration {

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
    public Dispatcher controllerDispatcherRequests(PathValidator pathValidator, ApplicationContext context) {
        List<Object> controllers = context.getBeansWithAnnotation(BotEndpoint.class).values().stream().toList();
        return new DefaultDispatcher(pathValidator, controllers);
    }


    @Bean
//    @ConditionalOnMissingBean(ErrorViewer.class)
    public ErrorViewer errorViewer() {
        return new DefaultErrorViewer();
    }

    @Bean
    public ExceptionHandler<ResponseException> responseExceptionExceptionHandler() {
        return new DefaultResponseExceptionHandler();
    }

    @Bean
//    @ConditionalOnMissingBean(MapperExceptionHandler.class)
    public MapperExceptionHandler mapperExceptionHandler(ApplicationContext context) {
        return new DefaultMapperExceptionHandlers(context);
    }

    @Bean
//    @ConditionalOnMissingBean(PathParser.class)
    public PathParser pathParser() {
        return new DefaultPathParser();
    }

    @Bean
 //   @ConditionalOnMissingBean(PathValidator.class)
    public PathValidator pathValidator() {
        return new DefaultPathValidator();
    }

    @Bean
 //   @ConditionalOnMissingBean(SessionModifier.class)
    public SessionModifier sessionModifier() {
        return new DefaultSessionModifier();
    }

    @Bean
//    @ConditionalOnMissingBean(SessionInitializer.class)
    public SessionInitializer sessionInitializer() {
        return new DefaultSessionInitializer();
    }

    @Bean
    //   @ConditionalOnMissingBean(SessionsProvider.class)
    public SessionsProvider sessionProvider(UpdateParserProvider updateParserProvider,
                                            SessionInitializer sessionInitializer,
                                            PathValidator pathValidator,
                                            PathParser pathParser) {
        return new SessionsProviderImpl(updateParserProvider, sessionInitializer, pathValidator, pathParser);
    }


    @Bean
    @ConditionalOnProperty(name = {"botToken", "botName"})
//    @ConditionalOnMissingBean(LongPollingBot.class)
    public LongPollingBot longPollingBot(@Value("${botToken}") String botToken,
                                         Dispatcher dispatcher,
                                         MethodExecutor methodExecutor,
                                         @Value("${botName}") String botName,
                                         SessionsProvider botSession,
                                         MapperExceptionHandler mapperExceptionHandler,
                                         ErrorViewer errorViewer,
                                         SessionModifier sessionModifier) {
        return new BotMVC(botToken, dispatcher, methodExecutor, botName,
                botSession, mapperExceptionHandler, errorViewer, sessionModifier);
    }

    @Bean
    @ConditionalOnBean(LongPollingBot.class)
    public BotSession botSession(LongPollingBot bot) throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
    }

    @Bean
    //   @ConditionalOnMissingBean(MethodExecutor.class)
    public MethodExecutor methodExecutor() {
        return new DefaultMethodExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


}
