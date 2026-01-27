package com.example.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "subtopics")
public class Subtopic {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_markdown", columnDefinition = "TEXT")
    private String contentMarkdown;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    protected Subtopic() {}

    public Subtopic(String id, String title, String contentMarkdown, Topic topic) {
        this.id = id;
        this.title = title;
        this.contentMarkdown = contentMarkdown;
        this.topic = topic;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContentMarkdown() { return contentMarkdown; }


}
