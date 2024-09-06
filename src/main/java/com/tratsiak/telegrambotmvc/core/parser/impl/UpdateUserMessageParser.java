package com.tratsiak.telegrambotmvc.core.parser.impl;

import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateUserMessageParser implements UpdateParser {

    @Override
    public Request parse(Update update) {
        Message message = update.getMessage();

        return Request.builder()
                .id(message.getChatId())
                .body(message.getText())
                .build();
    }

    @Override
    public UpdateParserType getType() {
        return UpdateParserType.USER_MESSAGE;
    }
}
