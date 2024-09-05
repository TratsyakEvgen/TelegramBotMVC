package com.tratsiak.telegrambotmvc.core.dispatcher;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;

import java.util.Optional;

public interface Dispatcher {
    void init();

    Optional<View> execute(Session session);

}
