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

        System.out.println("CONSOLEEE-START getProgress()");
        System.out.println("CONSOLEEE-enrollmentId = " + enrollmentId);

        // 🔐 Authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("CONSOLEEE-auth object = " + auth);

        if (auth == null) {
            System.out.println("CONSOLEEE-auth is NULL");
            throw new UnauthorizedAccessException();
        }

        String authenticatedUserId = auth.getName();
        System.out.println("CONSOLEEE-authenticatedUserId = " + authenticatedUserId);
        System.out.println("CONSOLEEE-authenticatedUserId class = "
                + authenticatedUserId.getClass().getName());

        // 1️⃣ Fetch enrollment
        System.out.println("CONSOLEEE-Fetching enrollment from DB");

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    System.out.println("CONSOLEEE-Enrollment NOT FOUND");
                    return new EnrollmentNotFoundException();
                });

        System.out.println("CONSOLEEE-Enrollment FOUND = " + enrollment);

        // 2️⃣ Enrollment user checks
        System.out.println("CONSOLEEE-enrollment.getUser() = " + enrollment.getUser());

        if (enrollment.getUser() == null) {
            System.out.println("CONSOLEEE-enrollment.getUser() is NULL");
            throw new UnauthorizedAccessException();
        }

        System.out.println("CONSOLEEE-enrollment.getUser().getId() = "
                + enrollment.getUser().getId());

        System.out.println("CONSOLEEE-enrollment.getUser().getId() class = "
                + enrollment.getUser().getId().getClass().getName());

        // 3️⃣ Ownership check (EMAIL ↔ EMAIL)
        System.out.println("CONSOLEEE-Comparing authenticatedUserId with enrollment email");

        if (!enrollment.getUser().getEmail().equals(authenticatedUserId)) {
            System.out.println("CONSOLEEE-OWNERSHIP CHECK FAILED");
            System.out.println("CONSOLEEE-authenticatedUserId = " + authenticatedUserId);
            System.out.println("CONSOLEEE-enrollmentUserEmail = "
                    + enrollment.getUser().getEmail());
            throw new UnauthorizedAccessException();
        }

        System.out.println("CONSOLEEE-OWNERSHIP CHECK PASSED");

        // 4️⃣ Course info
        String courseId = enrollment.getCourse().getId();
        String courseTitle = enrollment.getCourse().getTitle();

        System.out.println("CONSOLEEE-courseId = " + courseId);
        System.out.println("CONSOLEEE-courseTitle = " + courseTitle);

        // 5️⃣ Total subtopics
        System.out.println("CONSOLEEE-Counting total subtopics");
        long totalSubtopics = subtopicRepository.countByCourseId(courseId);
        System.out.println("CONSOLEEE-totalSubtopics = " + totalSubtopics);

        // 6️⃣ Completed subtopics
        System.out.println("CONSOLEEE-Fetching completed subtopics");

        List<SubtopicProgress> completedProgress =
                subtopicProgressRepository.findCompletedByUserAndCourse(
                       enrollment.getUser().getId(),
                        courseId
                );

        System.out.println("CONSOLEEE-completedProgress list size = "
                + (completedProgress == null ? "NULL" : completedProgress.size()));

        if (completedProgress != null) {
            completedProgress.forEach(sp ->
                    System.out.println("CONSOLEEE-completedSubtopicId = "
                            + sp.getSubtopic().getId()));
        }

        long completedCount = completedProgress == null ? 0 : completedProgress.size();

        // 7️⃣ Percentage calculation
        double percentage = totalSubtopics == 0
                ? 0.0
                : (completedCount * 100.0) / totalSubtopics;

        System.out.println("CONSOLEEE-percentage(raw) = " + percentage);

        // 8️⃣ DTO mapping
        System.out.println("CONSOLEEE-Mapping CompletedSubtopicDTOs");

        List<CompletedSubtopicDTO> completedItems =
                completedProgress == null
                        ? List.of()
                        : completedProgress.stream()
                        .map(sp -> {
                            System.out.println(
                                    "CONSOLEEE-Mapping subtopic "
                                            + sp.getSubtopic().getId()
                                            + " title=" + sp.getSubtopic().getTitle()
                            );
                            return new CompletedSubtopicDTO(
                                    sp.getSubtopic().getId(),
                                    sp.getSubtopic().getTitle(),
                                    sp.getCompletedAt()
                            );
                        })
                        .toList();

        double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
        System.out.println("CONSOLEEE-percentage(rounded) = " + roundedPercentage);

        System.out.println("CONSOLEEE-END getProgress()");

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
