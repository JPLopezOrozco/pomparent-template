package com.juan.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> InsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(409)).body(ex.getMessage());
    }

    @ExceptionHandler(NoAccountException.class)
    public ResponseEntity<String> NoAccountException(NoAccountException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(404)).body(ex.getMessage());
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<String> AccountException(AccountException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(400)).body(ex.getMessage());
    }


}
