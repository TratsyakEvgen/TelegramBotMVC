package com.tratsiak.telegrambotmvc.exception;


import com.tratsiak.telegrambotmvc.core.view.View;
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

}
