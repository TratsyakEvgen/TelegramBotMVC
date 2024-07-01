package com.tratsiak.telegrambotmvc.core.path.impl;


import com.tratsiak.telegrambotmvc.core.path.NotValidPathException;
import com.tratsiak.telegrambotmvc.core.path.PathValidator;


public class DefaultPathValidator implements PathValidator {

    @Override
    public void isValidPath(String path) throws NotValidPathException {
        validate(path, "(/[\\p{IsAlphabetic}\\d\\-]+)+");
    }

    @Override
    public void isValidPathWithParams(String path) throws NotValidPathException {
        validate(path, "(/[\\p{IsAlphabetic}\\d\\-]+)+(\\?([\\p{IsAlphabetic}\\d\\-]+=[\\p{IsAlphabetic}\\d\\-]+)" +
                "(&[\\p{IsAlphabetic}\\d\\-]+=[\\p{IsAlphabetic}\\d\\-]+)*)?");
    }


    private void validate(String path, String regex) throws NotValidPathException {
        if (path == null) {
            throw new NotValidPathException("Path is null");
        }

        int length = path.length();
        int maxLength = 64;

        if (length > maxLength) {
            throw new NotValidPathException("Path " + path +
                    " is not valid, max length should be " + maxLength + ", current length =" + length);
        }
        boolean isValid = path.matches(regex);
        if (!isValid) {
            throw new NotValidPathException("Path " + path + " is not valid");
        }
    }

}
