package org.example.lesson1First.exception.database;

import org.example.lesson1First.exception.superClasses.InvalidRequestParamException;

public class UniqueUserPhoneException extends InvalidRequestParamException {
    public UniqueUserPhoneException(String message) {
        super(message);
    }
}
