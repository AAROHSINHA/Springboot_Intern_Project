package com.example.demo.exceptions;

public class NotEnrolledException extends RuntimeException {
    public NotEnrolledException() {
        super("You must be enrolled in the course to track progress");
    }
}
    