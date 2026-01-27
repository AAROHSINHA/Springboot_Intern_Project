package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

record RegisterRequest(String email, String password) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // We just call the service. If it fails, GlobalExceptionHandler takes over.
        User user = authService.registerUser(request.email(), request.password());

        // Return 201 Created as per requirements
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "id", user.getId(),
            "email", user.getEmail(),
            "message", "User registered successfully"
        ));
    }
}