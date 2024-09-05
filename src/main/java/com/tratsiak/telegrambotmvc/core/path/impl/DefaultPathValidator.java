package com.tratsiak.telegrambotmvc.core.path.impl;


import com.tratsiak.telegrambotmvc.core.path.NotValidPathException;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;


public class DefaultPathValidator implements PathValidator {

    private final static int MAX_LENGTH = 64;

    @Override
    public void isValidPath(String path) {
        validate(path, "(/[\\p{IsAlphabetic}\\d\\-]+)+");
    }

    @Override
    public void isValidPathWithParams(String path) {
        validate(path, "(/[\\p{IsAlphabetic}\\d\\-]+)+(\\?([\\p{IsAlphabetic}\\d\\-]+=[\\p{IsAlphabetic}\\d\\-]+)" +
                "(&[\\p{IsAlphabetic}\\d\\-]+=[\\p{IsAlphabetic}\\d\\-]+)*)?");
    }


    private void validate(String path, String regex) {
        if (path == null) {
            throw new NotValidPathException("Path is null");
        }

        int pathLength = path.length();
        if (path.length() > MAX_LENGTH) {
            throw new NotValidPathException(String.format(
                    "Path %s is not valid, max length must be %d current length = %d", path, MAX_LENGTH, pathLength
            ));

        }
        boolean isValid = path.matches(regex);
        if (!isValid) {
            throw new NotValidPathException("Path " + path + " is not valid");
        }
    }

}
