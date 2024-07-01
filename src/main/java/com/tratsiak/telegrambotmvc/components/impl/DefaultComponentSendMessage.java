package com.tratsiak.telegrambotmvc.components.impl;


import com.tratsiak.telegrambotmvc.components.ComponentSendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;


public class DefaultComponentSendMessage implements ComponentSendMessage {
    @Override
    public SendMessage get(long id, String text) {
        return SendMessage.builder().chatId(id).text(text).build();
    }

    @Override
    public SendMessage get(long id, String text, ReplyKeyboard replyKeyboard) {
        return SendMessage.builder().chatId(id).text(text).replyMarkup(replyKeyboard).build();
    }

}
