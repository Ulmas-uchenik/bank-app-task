package org.example.lesson1First.exception.database.transaction;

public class TransactionAmountException extends RuntimeException {
    public TransactionAmountException(String message) {
        super(message);
    }
}
