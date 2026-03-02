package org.example.lesson1First.exception.superClasses;

public class NotFoundException extends UserInputException{
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException() {
    }
}
