package org.example.lesson1First.exception.database;

import org.example.lesson1First.exception.superClasses.InvalidRequestParamException;

public class UniqueBankAccountException extends InvalidRequestParamException {
    public UniqueBankAccountException(String message) {
        super(message);
    }
}
