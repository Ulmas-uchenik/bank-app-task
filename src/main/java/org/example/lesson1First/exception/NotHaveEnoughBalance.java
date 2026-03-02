package org.example.lesson1First.exception;

import org.example.lesson1First.exception.superClasses.UserInputException;

public class NotHaveEnoughBalance extends UserInputException {
    public NotHaveEnoughBalance(String message) {
        super(message);
    }
}
