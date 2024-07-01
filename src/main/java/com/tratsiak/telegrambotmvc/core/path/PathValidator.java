package com.tratsiak.telegrambotmvc.core.path;

public interface PathValidator {
    void isValidPath(String path) throws NotValidPathException;

    void isValidPathWithParams(String path) throws NotValidPathException;
}
