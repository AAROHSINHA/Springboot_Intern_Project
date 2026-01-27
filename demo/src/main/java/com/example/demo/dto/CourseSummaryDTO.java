package com.example.demo.dto;

public class CourseSummaryDTO {

    private String id;
    private String title;
    private String description;
    private long topicCount;
    private long subtopicCount;

    public CourseSummaryDTO(
            String id,
            String title,
            String description,
            long topicCount,
            long subtopicCount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topicCount = topicCount;
        this.subtopicCount = subtopicCount;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getTopicCount() { return topicCount; }
    public long getSubtopicCount() { return subtopicCount; }
}
