package com.tratsiak.telegrambotmvc.components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface ComponentInlineKeyboardButton {
    InlineKeyboardButton get(String text, String callbackData);
}
