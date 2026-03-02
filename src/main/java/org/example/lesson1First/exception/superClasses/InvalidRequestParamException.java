package org.example.lesson1First.exception.superClasses;

public class InvalidRequestParamException extends UserInputException{
    public InvalidRequestParamException(String message) {
        super(message);
    }

    public InvalidRequestParamException(Throwable cause) {
        super(cause);
    }

    public InvalidRequestParamException() {
    }
}
