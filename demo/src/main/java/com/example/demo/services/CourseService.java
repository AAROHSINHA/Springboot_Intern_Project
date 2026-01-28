package com.example.demo.services;

import com.example.demo.dto.CourseDetailDTO;
import com.example.demo.dto.CourseSummaryDTO;
import com.example.demo.dto.SubtopicDTO;
import com.example.demo.dto.TopicDTO;
import com.example.demo.entities.Course;
import com.example.demo.entities.Topic;
import com.example.demo.entities.Subtopic;
import com.example.demo.repositories.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public CourseService(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

    // seed data run on startup
    @Transactional
    public void seedCoursesIfEmpty() {
        // if database has no courses means bring in courses from the json
        // if database does have courses, means we don't need to run this function
        if(courseRepository.count() > 0) return;

            try {
        InputStream is = new ClassPathResource("data/courses.json").getInputStream();
        var root = objectMapper.readTree(is);

        for (var courseNode : root.get("courses")) {

            Course course = new Course(
                courseNode.get("id").asText(),
                courseNode.get("title").asText(),
                courseNode.get("description").asText()
            );

            Set<Topic> topics = new HashSet<>();

            for (var topicNode : courseNode.get("topics")) {

                Topic topic = new Topic(
                    topicNode.get("id").asText(),
                    topicNode.get("title").asText(),
                    course
                );

                Set<Subtopic> subtopics = new HashSet<>();

                for (var subNode : topicNode.get("subtopics")) {

                    Subtopic subtopic = new Subtopic(
                        subNode.get("id").asText(),
                        subNode.get("title").asText(),
                        subNode.get("content").asText(),
                        topic
                    );

                    subtopics.add(subtopic);
                }

                topic.setSubtopics(subtopics);
                topics.add(topic);
            }

            course.setTopics(topics);
            courseRepository.save(course);
        }

        System.out.println("Seeded courses, topics, and subtopics");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // get only course info
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }

    // get all course 
     public List<CourseSummaryDTO> getCourseSummaries() {

        List<Object[]> rows = courseRepository.fetchCourseSummaries();
        List<CourseSummaryDTO> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(new CourseSummaryDTO(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                ((Number) row[3]).longValue(),
                ((Number) row[4]).longValue()
            ));
        }

        return result;
    }
    
    // Get course by id
    
public CourseDetailDTO getCourseDetailsById(String id) {

    Course course = courseRepository.findCourseWithDetails(id);
    if (course == null) return null;

    List<TopicDTO> topicDTOs = new ArrayList<>();

    for (Topic topic : course.getTopics()) {

        List<SubtopicDTO> subtopicDTOs = new ArrayList<>();

        for (Subtopic sub : topic.getSubtopics()) {
            subtopicDTOs.add(new SubtopicDTO(
                sub.getId(),
                sub.getTitle(),
                sub.getContentMarkdown()
            ));
        }

        topicDTOs.add(new TopicDTO(
            topic.getId(),
            topic.getTitle(),
            subtopicDTOs
        ));
    }

    return new CourseDetailDTO(
        course.getId(),
        course.getTitle(),
        course.getDescription(),
        topicDTOs
    );
}
}
