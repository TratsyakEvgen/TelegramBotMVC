package com.tratsiak.telegrambotmvc.core.parser;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateParserProvider {
    UpdateParser getUpdateParser(Update update);
}
