package com.tratsiak.telegrambotmvc.core.parser.impl;

import com.tratsiak.telegrambotmvc.core.parser.UpdateParser;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserProvider;
import com.tratsiak.telegrambotmvc.core.parser.UpdateParserType;
import com.tratsiak.telegrambotmvc.core.parser.exception.UpdateParserProviderException;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class UpdateParserProviderImpl implements UpdateParserProvider {

    private final List<UpdateParser> updateParsers;

    private final Map<Predicate<Update>, Supplier<UpdateParser>> updateParserMap = new HashMap<>();

    {
        updateParserMap.put(
                Update::hasCallbackQuery,
                () -> this.getUpdateParserByType(UpdateParserType.CALLBACK_QUERY)
        );
        updateParserMap.put(
                update -> update.hasMessage() && update.getMessage().getWebAppData() != null,
                () -> this.getUpdateParserByType(UpdateParserType.WEB_APP)
        );

        updateParserMap.put(
                update -> update.hasMessage() && update.getMessage().isCommand(),
                () -> this.getUpdateParserByType(UpdateParserType.BOT_COMMAND)
        );

        updateParserMap.put(
                update -> {
                    Message message = update.getMessage();
                    return update.hasMessage() && !message.isCommand() && message.getWebAppData() == null;
                },
                () -> this.getUpdateParserByType(UpdateParserType.USER_MESSAGE)
        );
    }

    public UpdateParserProviderImpl(List<UpdateParser> updateParsers) {
        this.updateParsers = updateParsers;
    }


    @Override
    public UpdateParser getUpdateParser(Update update) {
        validate(update);
        return updateParserMap.entrySet()
                .stream()
                .filter(entry -> entry.getKey().test(update))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new UpdateParserProviderException("Not found update parser"))
                .get();
    }

    private UpdateParser getUpdateParserByType(UpdateParserType type) {
        return updateParsers.stream()
                .filter(updateParser -> updateParser.getType().equals(type))
                .findFirst()
                .orElseThrow();
    }

    private void validate(Update update) {
        if (update == null) {
            throw new UpdateParserProviderException("Update mustn't be null");
        }
    }
}
