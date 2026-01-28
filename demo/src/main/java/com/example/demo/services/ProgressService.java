package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.exceptions.*;



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

        // user from JWT email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //  Fetch subtopic
        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new SubtopicNotFoundException(subtopicId));

        //  chain: Subtopic -> Topic -> Course
        Course course = subtopic.getTopic().getCourse();

        //  Check enrollment
        if (!enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new NotEnrolledException();
        }

        // already completed?
        SubtopicProgress progress = progressRepository
                .findByUserAndSubtopic(user, subtopic)
                .orElseGet(() -> new SubtopicProgress(user, subtopic));

        // Mark completed 
        if (progress.getCompletedAt() == null) {
            progress.markCompleted();
        }

        return progressRepository.save(progress);
    }
}
