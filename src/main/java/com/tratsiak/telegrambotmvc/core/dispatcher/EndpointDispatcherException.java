package com.tratsiak.telegrambotmvc.core.dispatcher;

public class EndpointDispatcherException extends RuntimeException {
    public EndpointDispatcherException(String message) {
        super(message);
    }

    public EndpointDispatcherException(String message, Throwable cause) {
        super(message, cause);
    }

}
