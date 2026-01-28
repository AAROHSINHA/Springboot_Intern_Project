package com.example.demo.services;

import com.example.demo.dto.CompletedSubtopicDTO;
import com.example.demo.dto.EnrollmentProgressResponse;
import com.example.demo.entities.Enrollment;
import com.example.demo.entities.SubtopicProgress;
import com.example.demo.exceptions.EnrollmentNotFoundException;
import com.example.demo.exceptions.UnauthorizedAccessException;
import com.example.demo.repositories.EnrollmentRepository;
import com.example.demo.repositories.SubtopicProgressRepository;
import com.example.demo.repositories.SubtopicRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnrollmentProgressService {

    private final EnrollmentRepository enrollmentRepository;
    private final SubtopicRepository subtopicRepository;
    private final SubtopicProgressRepository subtopicProgressRepository;

    public EnrollmentProgressService(
            EnrollmentRepository enrollmentRepository,
            SubtopicRepository subtopicRepository,
            SubtopicProgressRepository subtopicProgressRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.subtopicRepository = subtopicRepository;
        this.subtopicProgressRepository = subtopicProgressRepository;
    }

    public EnrollmentProgressResponse getProgress(Long enrollmentId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UnauthorizedAccessException();
        }

        String authenticatedUserId = auth.getName();

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(EnrollmentNotFoundException::new);

        if (enrollment.getUser() == null) {
            throw new UnauthorizedAccessException();
        }

        if (!enrollment.getUser().getEmail().equals(authenticatedUserId)) {
            throw new UnauthorizedAccessException();
        }

        String courseId = enrollment.getCourse().getId();
        String courseTitle = enrollment.getCourse().getTitle();

        long totalSubtopics = subtopicRepository.countByCourseId(courseId);

        List<SubtopicProgress> completedProgress =
                subtopicProgressRepository.findCompletedByUserAndCourse(
                        enrollment.getUser().getId(),
                        courseId
                );

        long completedCount =
                completedProgress == null ? 0 : completedProgress.size();

        double percentage = totalSubtopics == 0
                ? 0.0
                : (completedCount * 100.0) / totalSubtopics;

        List<CompletedSubtopicDTO> completedItems =
                completedProgress == null
                        ? List.of()
                        : completedProgress.stream()
                        .map(sp -> new CompletedSubtopicDTO(
                                sp.getSubtopic().getId(),
                                sp.getSubtopic().getTitle(),
                                sp.getCompletedAt()
                        ))
                        .toList();

        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;

        return new EnrollmentProgressResponse(
                enrollmentId,
                courseId,
                courseTitle,
                totalSubtopics,
                completedCount,
                roundedPercentage,
                completedItems
        );
    }
}
