package com.tratsiak.telegrambotmvc.components.impl;

import com.tratsiak.telegrambotmvc.components.ComponentInlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class DefaultComponentInlineKeyboardButton implements ComponentInlineKeyboardButton {
    @Override
    public InlineKeyboardButton get(String text, String callbackData) {
        return InlineKeyboardButton.builder().text(text).callbackData(callbackData).build();
    }
}
