package com.tratsiak.telegrambotmvc.core.parser.impl;

import com.tratsiak.telegrambotmvc.core.parser.Request;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;


public class UpdateCallbackQueryParser implements UpdateParser {


    @Override
    public Request parse(Update update) {
        CallbackQuery query = update.getCallbackQuery();

        return Request.builder()
                .id(query.getFrom().getId())
                .path(query.getData())
                .build();
    }

    @Override
    public UpdateParserType getType() {
        return UpdateParserType.CALLBACK_QUERY;
    }
}
