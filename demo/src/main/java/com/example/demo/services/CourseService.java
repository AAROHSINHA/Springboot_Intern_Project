package com.example.demo.services;

import com.example.demo.entities.Course;
import com.example.demo.entities.Topic;
import com.example.demo.entities.Subtopic;
import com.example.demo.repositories.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    public CourseService(CourseRepository courseRepository, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.objectMapper = objectMapper;
    }

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

            List<Topic> topics = new ArrayList<>();

            for (var topicNode : courseNode.get("topics")) {

                Topic topic = new Topic(
                    topicNode.get("id").asText(),
                    topicNode.get("title").asText(),
                    course
                );

                List<Subtopic> subtopics = new ArrayList<>();

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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }
}
