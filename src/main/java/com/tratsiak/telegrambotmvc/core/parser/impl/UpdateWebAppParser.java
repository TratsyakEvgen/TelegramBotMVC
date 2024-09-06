package com.tratsiak.telegrambotmvc.core.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserType;
import com.tratsiak.telegrambotmvc.core.parser.WebApp;
import com.tratsiak.telegrambotmvc.core.parser.exception.UpdateParserException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateWebAppParser implements UpdateParser {
    private final ObjectMapper objectMapper;

    public UpdateWebAppParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Request parse(Update update) {
        Message message = update.getMessage();

        String data = message.getWebAppData().getData();
        WebApp webApp = deserialize(data);

        return Request.builder()
                .id(message.getChatId())
                .body(webApp.getData())
                .path(webApp.getCallBack()).build();
    }

    @Override
    public UpdateParserType getType() {
        return UpdateParserType.WEB_APP;
    }

    private WebApp deserialize(String data) {
        try {
            return objectMapper.readValue(data, WebApp.class);
        } catch (JsonProcessingException e) {
            throw new UpdateParserException("Can not parse WebApp " + data, e);
        }
    }
}
