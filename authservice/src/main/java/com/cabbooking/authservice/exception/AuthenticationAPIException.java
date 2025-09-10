package com.cabbooking.authservice.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public AuthenticationAPIException(HttpStatus status,String message) {
        super(message);
        this.status = status;
        this.message = message;
    }
}
