package com.example.demo.controllers;

import com.example.demo.entities.Enrollment;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.EnrollmentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;

    public EnrollmentController(
            EnrollmentService enrollmentService,
            UserRepository userRepository
    ) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{courseId}/enroll")
    public Map<String, Object> enroll(
            @PathVariable String courseId,
            Authentication authentication 
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Enrollment enrollment = enrollmentService.enroll(user, courseId);

        return Map.of(
                "enrollmentId", enrollment.getId(),
                "courseId", enrollment.getCourse().getId(),
                "courseTitle", enrollment.getCourse().getTitle(),
                "enrolledAt", enrollment.getEnrolledAt()
        );
    }
}
