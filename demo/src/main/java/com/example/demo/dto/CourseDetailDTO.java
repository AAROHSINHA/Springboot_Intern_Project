package com.example.demo.dto;

import java.util.List;

public class CourseDetailDTO {
    private String id;
    private String title;
    private String description;
    private List<TopicDTO> topics;

    public CourseDetailDTO(
            String id,
            String title,
            String description,
            List<TopicDTO> topics
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topics = topics;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<TopicDTO> getTopics() { return topics; }
}
