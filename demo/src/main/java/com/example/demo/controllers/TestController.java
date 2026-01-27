package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<?> whoAmI() {
        // Get the email we stored in the Filter
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String email) {
            return ResponseEntity.ok(Map.of(
                "message", "Hey! You are logged in.",
                "email", email
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
    }
}