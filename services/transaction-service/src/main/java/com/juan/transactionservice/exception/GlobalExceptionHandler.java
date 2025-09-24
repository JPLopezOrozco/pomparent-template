package com.juan.transactionservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AccountNotFoundException.class)
    public ResponseEntity<ProblemDetail> accountNotFound(AccountNotFoundException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Account Not Found");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:account-not-found"));
        pd.setProperty("code", "Account not found");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(value = CurrencyException.class)
    public ResponseEntity<ProblemDetail> currencyException(CurrencyException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Currency Error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:currency-error"));
        pd.setProperty("code", "Currency Error");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(value = TransactionNotFoundException.class)
    public ResponseEntity<ProblemDetail> transactionNotException(TransactionNotFoundException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Transaction Not Found");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:transaction-not-found"));
        pd.setProperty("code", "Transaction Not Found");
        pd.setProperty("instance", rq.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(value = IdempotencyKeyException.class)
    public ResponseEntity<ProblemDetail> idempotencyKeyExistException(IdempotencyKeyException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Idempotency Key Error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:idempotency-key-error"));
        pd.setProperty("code", "Idempotency Key Error");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(value = AccountException.class)
    public ResponseEntity<ProblemDetail> noActiveAccount(AccountException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Account Error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:account-error"));
        pd.setProperty("code", "Account Error");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(value = NoBalanceException.class)
    public ResponseEntity<ProblemDetail> noBalanceException(NoBalanceException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("No Balance Error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:no-balance-error"));
        pd.setProperty("code", "No Balance Error");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(value = TransactionException.class)
    public ResponseEntity<ProblemDetail> transactionException(TransactionException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Transaction Error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:transaction-error"));
        pd.setProperty("code", "Transaction Error");
        pd.setProperty("instance", rq.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }


}
