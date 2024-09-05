package com.tratsiak.telegrambotmvc.core.session.impl.parser;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateParser {

    Request parse(Update update);

    UpdateParserType getType();
}
