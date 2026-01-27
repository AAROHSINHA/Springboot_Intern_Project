package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.exceptions.*;

import java.time.Instant;

@Service
public class ProgressService {

    private final UserRepository userRepository;
    private final SubtopicRepository subtopicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubtopicProgressRepository progressRepository;

    public ProgressService(
            UserRepository userRepository,
            SubtopicRepository subtopicRepository,
            EnrollmentRepository enrollmentRepository,
            SubtopicProgressRepository progressRepository
    ) {
        this.userRepository = userRepository;
        this.subtopicRepository = subtopicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional
    public SubtopicProgress markCompleted(String userEmail, String subtopicId) {

        // 1️⃣ Resolve user from JWT email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Fetch subtopic
        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new SubtopicNotFoundException(subtopicId));

        // 3️⃣ Walk the chain: Subtopic → Topic → Course
        Course course = subtopic.getTopic().getCourse();

        // 4️⃣ Check enrollment
        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new NotEnrolledException();
        }

        // 5️⃣ Idempotency: already completed?
        SubtopicProgress progress = progressRepository
                .findByUserAndSubtopic(user, subtopic)
                .orElseGet(() -> new SubtopicProgress(user, subtopic));

        // 6️⃣ Mark completed (safe to repeat)
        if (progress.getCompletedAt() == null) {
            progress.markCompleted();
        }

        return progressRepository.save(progress);
    }
}
