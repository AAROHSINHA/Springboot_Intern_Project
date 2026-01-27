package com.example.demo.repositories;

import com.example.demo.entities.Enrollment;
import com.example.demo.entities.User;
import com.example.demo.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Checks if an enrollment already exists for this user + course
    // Used to prevent duplicate enrollments
    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    // Used to verify enrollment
    boolean existsByUserAndCourse(User user, Course course);
}
