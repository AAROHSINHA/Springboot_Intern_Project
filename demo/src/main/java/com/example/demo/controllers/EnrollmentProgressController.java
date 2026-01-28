package com.example.demo.controllers;

import com.example.demo.dto.EnrollmentProgressResponse;
import com.example.demo.services.EnrollmentProgressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentProgressController {

    private final EnrollmentProgressService progressService;

    public EnrollmentProgressController(EnrollmentProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/{enrollmentId}/progress")
    public EnrollmentProgressResponse getProgress(
            @PathVariable Long enrollmentId
    ) {
        return progressService.getProgress(enrollmentId);
    }
}
