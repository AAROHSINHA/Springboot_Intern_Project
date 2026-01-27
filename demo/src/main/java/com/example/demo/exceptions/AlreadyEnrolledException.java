package com.example.demo.exceptions;

public class AlreadyEnrolledException extends RuntimeException {

    public AlreadyEnrolledException() {
        super("You are already enrolled in this course");
    }
}
    