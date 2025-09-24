package com.juan.accountservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ProblemDetail> InsufficientBalanceException(InsufficientBalanceException ex, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Insufficient Balance");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("urn:problem:insufficient-funds"));
        pd.setProperty("code", "Insufficient Balance");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(NoAccountException.class)
    public ResponseEntity<ProblemDetail> NoAccountException(NoAccountException ex, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Account Not Found");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("urn:problem:account-not-found"));
        pd.setProperty("code", "Account Not Found");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ProblemDetail> AccountException(AccountException ex, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Account Exception");
        pd.setDetail(ex.getMessage());
        pd.setType(URI.create("urn:problem:account"));
        pd.setProperty("code", "Account exception");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }


}
