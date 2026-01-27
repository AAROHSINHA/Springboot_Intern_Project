package com.example.demo.dto;

// Each match: topic/subtopic/content
public class MatchDetail {
    private String type; // "topic", "subtopic", "content"
    private String topicTitle;
    private String subtopicId;
    private String subtopicTitle;
    private String snippet;

    public MatchDetail(String type, String topicTitle, String subtopicId, String subtopicTitle, String snippet) {
        this.type = type;
        this.topicTitle = topicTitle;
        this.subtopicId = subtopicId;
        this.subtopicTitle = subtopicTitle;
        this.snippet = snippet;
    }

    // getters
    public String getType() { return type; }
    public String getTopicTitle() { return topicTitle; }
    public String getSubtopicId() { return subtopicId; }
    public String getSubtopicTitle() { return subtopicTitle; }
    public String getSnippet() { return snippet; }
}
