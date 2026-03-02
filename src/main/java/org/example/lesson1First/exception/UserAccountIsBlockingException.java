package org.example.lesson1First.exception;

import org.example.lesson1First.exception.superClasses.UserInputException;

public class UserAccountIsBlockingException extends UserInputException {
    public UserAccountIsBlockingException(String message) {
        super(message);
    }
}
