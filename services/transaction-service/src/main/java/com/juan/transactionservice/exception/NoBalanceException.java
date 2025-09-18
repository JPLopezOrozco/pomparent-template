package com.juan.transactionservice.exception;

public class NoBalanceException extends RuntimeException {
    public NoBalanceException(String message) {
        super(message);
    }
}
