package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.*;
import java.util.Optional;
import java.util.List;

public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {

    Optional<SubtopicProgress> findByUserAndSubtopic(User user, Subtopic subtopic);
    @Query("""
        SELECT sp
        FROM SubtopicProgress sp
        WHERE sp.user.id = :userId
          AND sp.completedAt IS NOT NULL
          AND sp.subtopic.topic.course.id = :courseId
    """)
    List<SubtopicProgress> findCompletedByUserAndCourse(
        @Param("userId") String userId,
        @Param("courseId") String courseId
    );
}
