package com.example.demo.dto;

import java.util.List;

public class TopicDTO {
    private String id;
    private String title;
    private List<SubtopicDTO> subtopics;

    public TopicDTO(String id, String title, List<SubtopicDTO> subtopics) {
        this.id = id;
        this.title = title;
        this.subtopics = subtopics;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<SubtopicDTO> getSubtopics() { return subtopics; }
}
