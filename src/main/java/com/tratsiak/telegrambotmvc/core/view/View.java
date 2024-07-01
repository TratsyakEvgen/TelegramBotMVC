package com.tratsiak.telegrambotmvc.core.view;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.List;

public interface View {
    List<PartialBotApiMethod<?>> getMessages();
}
