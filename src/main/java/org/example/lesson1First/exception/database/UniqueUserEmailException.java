package org.example.lesson1First.exception.database;

import org.example.lesson1First.exception.superClasses.InvalidRequestParamException;

public class UniqueUserEmailException extends InvalidRequestParamException {
    public UniqueUserEmailException(String message) {
        super(message);
    }
}
