package com.example.demo.services;

import com.example.demo.entities.Course;
import com.example.demo.entities.Enrollment;
import com.example.demo.entities.User;
import com.example.demo.repositories.CourseRepository;
import com.example.demo.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exceptions.AlreadyEnrolledException;
import com.example.demo.exceptions.CourseNotFoundException;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public Enrollment enroll(User user, String courseId) {

        // 1. Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        // 2. Check if already enrolled
        enrollmentRepository.findByUserAndCourse(user, course)
                .ifPresent(e -> {
                    throw new AlreadyEnrolledException();
                });

        // 3. Create enrollment
        Enrollment enrollment = new Enrollment(user, course);

        // 4. Save to DB
        return enrollmentRepository.save(enrollment);
    }
}
