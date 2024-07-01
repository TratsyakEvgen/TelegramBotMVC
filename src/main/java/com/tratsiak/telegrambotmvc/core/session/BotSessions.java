package com.tratsiak.telegrambotmvc.core.session;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotSessions {
    Session getSession(Update update);
}
