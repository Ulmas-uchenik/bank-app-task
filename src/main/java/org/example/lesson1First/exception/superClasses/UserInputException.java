package org.example.lesson1First.exception.superClasses;

public class UserInputException extends RuntimeException{
    public UserInputException(String message) {
        super(message);
    }

    public UserInputException(Throwable cause) {
        super(cause);
    }

    public UserInputException() {
    }
}
