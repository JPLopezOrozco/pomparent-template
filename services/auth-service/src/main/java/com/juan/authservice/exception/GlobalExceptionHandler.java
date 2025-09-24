package com.juan.authservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<ProblemDetail> invalidTokenException(InvalidTokenException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED.value());
        pd.setTitle("Invalid token");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:invalid-token"));
        pd.setProperty("instance", rq.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }

    @ExceptionHandler(value = LoginException.class)
    public ResponseEntity<ProblemDetail> loginException(LoginException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED.value());
        pd.setTitle("Login error");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:login-error"));
        pd.setProperty("instance", rq.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }

    @ExceptionHandler(value = UserAlreadyExistException.class)
    public ResponseEntity<ProblemDetail> userAlreadyExistException(UserAlreadyExistException e, HttpServletRequest rq) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT.value());
        pd.setTitle("User already exist");
        pd.setDetail(e.getMessage());
        pd.setType(URI.create("urn:problem:user-exist"));
        pd.setProperty("instance", rq.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }


}
