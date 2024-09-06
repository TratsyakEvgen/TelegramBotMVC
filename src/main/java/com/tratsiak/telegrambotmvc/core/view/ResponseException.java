package com.tratsiak.telegrambotmvc.core.view;


import lombok.Getter;

@Getter
public class ResponseException extends RuntimeException {

    private View view;

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(View view) {
        this.view = view;
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(View view, Throwable cause) {
        super(cause);
        this.view = view;
    }
}
