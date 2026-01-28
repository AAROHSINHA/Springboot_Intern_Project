package com.example.demo.repositories;

import com.example.demo.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
      // search across course, topic, subtopic fields
  @Query(value = """
    SELECT DISTINCT c.*
    FROM courses c
    LEFT JOIN topics t ON t.course_id = c.id
    LEFT JOIN subtopics s ON s.topic_id = t.id
    WHERE EXISTS (
        SELECT 1
        FROM unnest(string_to_array(:query, ' ')) AS q
        WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', q, '%'))
           OR LOWER(c.description) LIKE LOWER(CONCAT('%', q, '%'))
           OR LOWER(t.title) LIKE LOWER(CONCAT('%', q, '%'))
           OR LOWER(s.title) LIKE LOWER(CONCAT('%', q, '%'))
           OR LOWER(s.content_markdown) LIKE LOWER(CONCAT('%', q, '%'))
    )
    """, nativeQuery = true)
List<Course> searchCourses(@Param("query") String query);


    

    // query that simply fetches all course ()
    @Query(value = """
    SELECT 
        c.id AS id,
        c.title AS title,
        c.description AS description,
        COUNT(DISTINCT t.id) AS topicCount,
        COUNT(s.id) AS subtopicCount
    FROM courses c
    LEFT JOIN topics t ON t.course_id = c.id
    LEFT JOIN subtopics s ON s.topic_id = t.id
    GROUP BY c.id
""", nativeQuery = true)
List<Object[]> fetchCourseSummaries();

    // query that finds course from id
    @Query("""
        SELECT DISTINCT c
        FROM Course c
        LEFT JOIN FETCH c.topics t
        LEFT JOIN FETCH t.subtopics
        WHERE c.id = :id
    """)
    Course findCourseWithDetails(@Param("id") String id);


}
