package com.example.demo.dto;

import java.time.Instant;

public class CompletedSubtopicDTO {

    private String subtopicId;
    private String subtopicTitle;
    private Instant completedAt;

    public CompletedSubtopicDTO(String subtopicId, String subtopicTitle, Instant completedAt) {
        this.subtopicId = subtopicId;
        this.subtopicTitle = subtopicTitle;
        this.completedAt = completedAt;
    }

    public String getSubtopicId() { return subtopicId; }
    public String getSubtopicTitle() { return subtopicTitle; }
    public Instant getCompletedAt() { return completedAt; }
}
