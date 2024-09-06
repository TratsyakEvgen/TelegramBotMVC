package com.tratsiak.telegrambotmvc.core.parser.impl;

import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateBotCommandParser implements UpdateParser {
    @Override
    public Request parse(Update update) {
        Message message = update.getMessage();

        return Request.builder()
                .id(message.getChatId())
                .path(message.getText())
                .build();
    }

    @Override
    public UpdateParserType getType() {
        return UpdateParserType.BOT_COMMAND;
    }
}
