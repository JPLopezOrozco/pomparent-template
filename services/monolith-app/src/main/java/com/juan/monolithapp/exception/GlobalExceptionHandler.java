package com.juan.monolithapp.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> AccountNotFounded(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> InsufficientBalance(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(409)).body(ex.getMessage());
    }

    @ExceptionHandler(NoTransactionException.class)
    public ResponseEntity<String> NoTransaction(NoTransactionException ex) {
        return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(ex.getMessage());
    }
}
