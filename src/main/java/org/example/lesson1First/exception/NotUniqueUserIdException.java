package org.example.lesson1First.exception;

import org.example.lesson1First.exception.superClasses.InvalidRequestParamException;

public class NotUniqueUserIdException extends InvalidRequestParamException {
    public NotUniqueUserIdException(String message) {super(message);
    }
}
