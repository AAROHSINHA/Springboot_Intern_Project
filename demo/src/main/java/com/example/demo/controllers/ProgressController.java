package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.SubtopicProgress;
import com.example.demo.services.ProgressService;

import java.util.Map;

@RestController
@RequestMapping("/api/subtopics")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/{subtopicId}/complete")
    public ResponseEntity<?> completeSubtopic(@PathVariable String subtopicId) {

        // Extract user email from JWT (set by JwtFilter)
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        SubtopicProgress progress =
                progressService.markCompleted(email, subtopicId);

        return ResponseEntity.ok(Map.of(
                "subtopicId", subtopicId,
                "completed", true,
                "completedAt", progress.getCompletedAt()
        ));
    }
}   
