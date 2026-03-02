package org.example.lesson1First.exception;

import org.example.lesson1First.exception.superClasses.NotFoundException;

public class NotFoundUserException extends NotFoundException {
    public NotFoundUserException(String message) {
        super(message);
    }
}
