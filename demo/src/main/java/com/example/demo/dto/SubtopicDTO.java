package com.example.demo.dto;

public class SubtopicDTO {
    private String id;
    private String title;
    private String content;

    public SubtopicDTO(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
}
