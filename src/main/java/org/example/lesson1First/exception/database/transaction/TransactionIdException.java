package org.example.lesson1First.exception.database.transaction;

public class TransactionIdException extends RuntimeException {
    public TransactionIdException(String message) {
        super(message);
    }
}
