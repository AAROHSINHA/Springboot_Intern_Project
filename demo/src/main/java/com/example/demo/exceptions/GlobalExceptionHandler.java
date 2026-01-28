package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.Instant;
import java.util.Map;

@ControllerAdvice // This annotation makes this class a "Global Safety Net"
public class GlobalExceptionHandler {

    // Helper method to build the specific JSON format required
    private ResponseEntity<Object> buildResponse(HttpStatus status, String errorType, String message) {
        Map<String, Object> body = Map.of(
            "error", errorType,
            "message", message,
            "timestamp", Instant.now().toString()
        );
        return new ResponseEntity<>(body, status);
    }

    // Catch  "UserAlreadyExists" = 409 Conflict
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserExists(UserAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    // Catch generic errors (like missing fields) = 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleBadRequest(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // Catch everything else =  500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralError(Exception ex) {
         ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", "An unexpected error occurred");
    }



@ExceptionHandler(AlreadyEnrolledException.class)
public ResponseEntity<Object> handleAlreadyEnrolled(AlreadyEnrolledException ex) {
    return buildResponse(
        HttpStatus.CONFLICT,
        "Already enrolled",
        ex.getMessage()
    );
}
@ExceptionHandler(CourseNotFoundException.class)
public ResponseEntity<Object> handleCourseNotFound(CourseNotFoundException ex) {
    return buildResponse(
        HttpStatus.NOT_FOUND,
        "Course not found",
        ex.getMessage()
    );
}
@ExceptionHandler(SubtopicNotFoundException.class)
    public ResponseEntity<Object> handleSubtopicNotFound(SubtopicNotFoundException ex) {
        return buildResponse(
            HttpStatus.NOT_FOUND, 
            "Subtopic Not Found", 
            ex.getMessage()
        );
    }
    @ExceptionHandler(NotEnrolledException.class)
    public ResponseEntity<Object> handleNotEnrolled(NotEnrolledException ex) {
        return buildResponse(
            HttpStatus.FORBIDDEN, 
            "Access Denied", 
            ex.getMessage()
        );
    }
    @ExceptionHandler(UnauthorizedAccessException.class)
public ResponseEntity<Object> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
    return buildResponse(
        HttpStatus.FORBIDDEN,
        "Unauthorized",
        ex.getMessage()
    );
}
@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<Object> handleInvalidCredentials(InvalidCredentialsException ex) {
    return buildResponse(
        HttpStatus.UNAUTHORIZED,
        "Unauthorized",
        ex.getMessage()
    );
}

@ExceptionHandler(InvalidInputException.class)
public ResponseEntity<Object> handleInvalidInput(InvalidInputException ex) {
    return buildResponse(
        HttpStatus.BAD_REQUEST,
        "Bad Request",
        ex.getMessage()
    );
}

@ExceptionHandler(AuthInternalException.class)
public ResponseEntity<Object> handleAuthInternal(AuthInternalException ex) {
    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Server Error",
        ex.getMessage()
    );
}


}