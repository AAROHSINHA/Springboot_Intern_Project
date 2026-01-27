package com.example.demo.exceptions;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(String courseId) {
        super("Course with id '" + courseId + "' does not exist");
    }
}
