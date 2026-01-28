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
            -- This generates the 'snippet' and wraps the match in <b> tags
            ts_headline('english', s.content_markdown, plainto_tsquery('english', :query), 
                'StartSel=<b>, StopSel=</b>, MaxWords=30, MinWords=15') as snippet,
            -- Logic to determine if the match was in the title or the content
            CASE 
                WHEN to_tsvector('english', s.title) @@ plainto_tsquery('english', :query) THEN 'subtopic'
                ELSE 'content'
            END as matchType
        FROM subtopics s
        JOIN topics t ON s.topic_id = t.id
        JOIN courses c ON t.course_id = c.id
        WHERE 
            to_tsvector('english', s.title || ' ' || s.content_markdown) @@ plainto_tsquery('english', :query)
            OR to_tsvector('english', t.title) @@ plainto_tsquery('english', :query)
            OR to_tsvector('english', c.title) @@ plainto_tsquery('english', :query)
    """, nativeQuery = true)
    List<SearchProjection> performFullTextSearch(@Param("query") String query);

    interface SearchProjection {
        String getCourseId();
        String getCourseTitle();
        String getTopicTitle();
        String getSubtopicId();
        String getSubtopicTitle();
        String getSnippet();
        String getMatchType();
    }

    @Query("""
        SELECT COUNT(s)
        FROM Subtopic s
        WHERE s.topic.course.id = :courseId
    """)
    long countByCourseId(@Param("courseId") String courseId);
}
