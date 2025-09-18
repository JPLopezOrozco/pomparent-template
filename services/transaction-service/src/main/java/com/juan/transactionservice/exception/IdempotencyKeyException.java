package com.juan.transactionservice.exception;

public class IdempotencyKeyException extends RuntimeException {
    public IdempotencyKeyException(String message) {
        super(message);
    }
}
