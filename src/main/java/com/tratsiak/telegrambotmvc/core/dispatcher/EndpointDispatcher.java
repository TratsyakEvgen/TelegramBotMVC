package com.tratsiak.telegrambotmvc.core.dispatcher;


import com.tratsiak.telegrambotmvc.core.session.Session;
import com.tratsiak.telegrambotmvc.core.view.View;

public interface EndpointDispatcher {

    View invoke(Session session);

}
