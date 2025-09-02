package com.juan.monolithapp.exception;

public class NoTransactionException extends RuntimeException {
    public NoTransactionException(String message) {
        super(message);
    }
}
