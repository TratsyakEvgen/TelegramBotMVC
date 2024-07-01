package com.tratsiak.telegrambotmvc.core.dispatcher;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;

import java.util.Optional;

public interface DispatcherRequests {
    Optional<View> executeMethod(Session session);

    void init();
}
