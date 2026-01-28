package com.example.demo.exceptions;

public class AuthInternalException extends RuntimeException {
    public AuthInternalException() {
        super("Authentication service failure");
    }
}
