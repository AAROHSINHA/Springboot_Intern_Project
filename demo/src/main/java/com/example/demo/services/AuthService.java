package com.example.demo.services;

import com.example.demo.entities.User;
import com.example.demo.exceptions.*;
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
    private final JwtUtil jwtUtil; 


    public AuthService(UserRepository userRepository, 
                       BCryptPasswordEncoder passwordEncoder, 
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ==> register user function
    public User registerUser(String email, String password) {
        // We vaildate the recieved inputs. If email or password is blank, throw error
        // for now constraint on password length is 5
        if (email == null || email.isBlank())  throw new InvalidInputException("Email is required");
        if (password == null || password.length() < 5)  throw new InvalidInputException("Password must be at least 5 characters");

        //  Check if email exists. If user exists we cannot move forward and throw an error
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("User with email '" + email + "' already exists");
        }

        //  Create User
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, hashedPassword);

        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, UUID.randomUUID().toString());
        } catch (Exception e) {
              throw new AuthInternalException();
        }
        // adding user to database
        return userRepository.save(user);
    }

    // ===> login route function
    public Map<String, Object> loginUser(String email, String password) {
        // simply find user in database by existing email. If user exists, check
        // the password. if correct, return the token else, error
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return Map.of(
            "token", jwtUtil.generateToken(user.getEmail()),
            "email", user.getEmail(),
            "expiresIn", 86400
        );
    }
}