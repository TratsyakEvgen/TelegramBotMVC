package com.tratsiak.telegrambotmvc.core.view;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {
    private long id;
    private String parseMode = ParseMode.HTML;
    private boolean isKeyboardButton = false;
    private boolean isKeyboardMarkup = false;
    private String text;
    private InlineKeyboardMarkup.InlineKeyboardMarkupBuilder inlineKeyboardMarkupBuilder;
    private List<InlineKeyboardButton> inlineKeyboardButtons;
    private ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder replyKeyboardMarkupBuilder;
    private List<KeyboardButton> keyboardButtons;

    public MessageBuilder id(long id) {
        this.id = id;
        return this;
    }

    public MessageBuilder disableHTML() {
        this.parseMode = null;
        return this;
    }

    public MessageBuilder text(String text) {
        this.text = text;
        return this;
    }

    public <T extends InlineKeyboardButton> MessageBuilder button(T button) {
        isKeyboardButton = true;
        if (inlineKeyboardMarkupBuilder == null) {
            inlineKeyboardMarkupBuilder = InlineKeyboardMarkup.builder();
        }
        if (inlineKeyboardButtons == null) {
            inlineKeyboardButtons = new ArrayList<>();
        }
        inlineKeyboardButtons.add(button);
        return this;
    }

    public <T extends KeyboardButton> MessageBuilder button(T button) {
        isKeyboardMarkup = true;
        if (replyKeyboardMarkupBuilder == null) {
            replyKeyboardMarkupBuilder = ReplyKeyboardMarkup.builder();
        }
        if (keyboardButtons == null) {
            keyboardButtons = new ArrayList<>();
        }
        keyboardButtons.add(button);
        return this;
    }

    public MessageBuilder nextRow() {
        if (isKeyboardButton) {
            inlineKeyboardMarkupBuilder.keyboardRow(inlineKeyboardButtons);
            inlineKeyboardButtons = null;
            return this;
        }
        replyKeyboardMarkupBuilder.keyboardRow(new KeyboardRow(keyboardButtons));
        keyboardButtons = null;
        return this;
    }

    public SendMessage build() {
        SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(id)
                .text(text)
                .parseMode(parseMode);

        if (!isKeyboardButton && !isKeyboardMarkup) {
            return builder.build();
        }

        if (isKeyboardButton) {
            if (inlineKeyboardButtons != null) {
                inlineKeyboardMarkupBuilder.keyboardRow(inlineKeyboardButtons);
            }
            return builder.replyMarkup(inlineKeyboardMarkupBuilder.build()).build();
        }

        if (keyboardButtons != null) {
            replyKeyboardMarkupBuilder.keyboardRow(new KeyboardRow(keyboardButtons));
        }
        return builder.replyMarkup(replyKeyboardMarkupBuilder.resizeKeyboard(true).build()).build();
    }


}
