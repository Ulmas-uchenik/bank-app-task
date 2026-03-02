package org.example.lesson1First.exception.database.transaction;

public class TransactionNotHaveTargetException extends RuntimeException {
    public TransactionNotHaveTargetException(String message) {
        super(message);
    }
}
