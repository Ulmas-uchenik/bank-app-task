package org.example.lesson1First.exception;

import org.example.lesson1First.exception.superClasses.NotFoundException;

public class NotFoundUserBankAccountException extends NotFoundException {
    public NotFoundUserBankAccountException(String message) {
        super(message);
    }
}
