package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtopic> subtopics;

    protected Topic() {}

    public Topic(String id, String title, Course course) {
        this.id = id;
        this.title = title;
        this.course = course;
    }
    public void setSubtopics(List<Subtopic> subtopics) {
    this.subtopics = subtopics;
}


    public String getId() { return id; }
    public String getTitle() { return title; }
    public Course getCourse() { return course; }
}
