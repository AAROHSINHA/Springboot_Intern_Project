package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String email, String password) {
        // 1. Validate input (will trigger 400 Bad Request via our Safety Net)
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");

        // 2. Check if email exists (will trigger 409 Conflict via our Safety Net)
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email '" + email + "' already exists");
        }

        // 3. Create User (Using UUID because your Entity ID is a String)
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword);
        
        // Manual ID setting since we aren't using @GeneratedValue
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RuntimeException("Internal Error setting ID");
        }

        return userRepository.save(user);
    }
}