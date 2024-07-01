package com.tratsiak.telegrambotmvc.components.impl;


import com.tratsiak.telegrambotmvc.components.ComponentSendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;


public class DefaultComponentSendAudio implements ComponentSendAudio {
    @Override
    public SendAudio get(long id, File file) {
        return SendAudio.builder()
                .audio(new InputFile(file))
                .chatId(id)
                .build();
    }

}
