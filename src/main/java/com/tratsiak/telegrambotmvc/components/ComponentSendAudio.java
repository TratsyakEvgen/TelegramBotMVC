package com.tratsiak.telegrambotmvc.components;

import org.telegram.telegrambots.meta.api.methods.send.SendAudio;

import java.io.File;


public interface ComponentSendAudio {
    SendAudio get(long id, File file);
}
