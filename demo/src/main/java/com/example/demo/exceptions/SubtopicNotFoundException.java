package com.example.demo.exceptions;

public class SubtopicNotFoundException extends RuntimeException {
    public SubtopicNotFoundException(String id) {
        super("Subtopic with id '" + id + "' does not exist");
    }
}
