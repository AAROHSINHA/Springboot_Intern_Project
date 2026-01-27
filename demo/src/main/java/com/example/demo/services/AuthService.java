package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.repositories.UserRepository;
import com.example.demo.config.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Map;

@Service
public class AuthService {

   private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // 1. DECLARE the variable here

    // 2. ADD them to the constructor so Spring can inject them
    public AuthService(UserRepository userRepository, 
                       BCryptPasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
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

    public Map<String, Object> loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Matches raw input password with the hashed password in DB
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return Map.of(
            "token", jwtUtil.generateToken(user.getEmail()),
            "email", user.getEmail(),
            "expiresIn", 86400
        );
    }
}