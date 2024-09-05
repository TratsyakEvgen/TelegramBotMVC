package com.tratsiak.telegrambotmvc.core.path;

public interface PathValidator {
    void isValidPath(String path);

    void isValidPathWithParams(String path);
}
