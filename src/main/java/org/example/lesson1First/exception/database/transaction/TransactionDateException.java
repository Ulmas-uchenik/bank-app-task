package org.example.lesson1First.exception.database.transaction;

public class TransactionDateException extends RuntimeException {
    public TransactionDateException(String message) {
        super(message);
    }
}
