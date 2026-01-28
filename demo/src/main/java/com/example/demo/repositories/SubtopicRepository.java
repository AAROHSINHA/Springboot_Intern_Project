package com.example.demo.repositories;

import com.example.demo.entities.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
@Query(value = """
        SELECT 
            c.id as courseId, 
            c.title as courseTitle, 
            t.title as topicTitle, 
            s.id as subtopicId, 
            s.title as subtopicTitle,
            
            -- 1. MATCH TYPE DETERMINATION
            CASE 
                WHEN s.title ILIKE CONCAT('%', :query, '%') THEN 'subtopic'
                WHEN s.content_markdown ILIKE CONCAT('%', :query, '%') THEN 'content'
                ELSE 'reference' 
            END as matchType,

            -- 2. SNIPPET GENERATION
            CASE 
                WHEN s.content_markdown ILIKE CONCAT('%', :query, '%') THEN 
                    CONCAT('...', SUBSTRING(
                        s.content_markdown, 
                        GREATEST(1, POSITION(LOWER(:query) IN LOWER(s.content_markdown)) - 20), 
                        100
                    ), '...')
                ELSE 
                    -- Fixed: Changed s.description to c.description
                    CONCAT(LEFT(c.description, 100), '...')
            END as snippet

        FROM subtopics s
        JOIN topics t ON s.topic_id = t.id
        JOIN courses c ON t.course_id = c.id
        WHERE 
            s.title ILIKE CONCAT('%', :query, '%')
            OR s.content_markdown ILIKE CONCAT('%', :query, '%')
            OR t.title ILIKE CONCAT('%', :query, '%')
            OR c.title ILIKE CONCAT('%', :query, '%')
    """, nativeQuery = true)
    List<SearchProjection> performFullTextSearch(@Param("query") String query);

    interface SearchProjection {
        String getCourseId();
        String getCourseTitle();
        String getTopicTitle();
        String getSubtopicId();
        String getSubtopicTitle();
        String getMatchType();
        String getSnippet();
    }
    @Query("""
        SELECT COUNT(s)
        FROM Subtopic s
        WHERE s.topic.course.id = :courseId
    """)
    long countByCourseId(@Param("courseId") String courseId);
}
