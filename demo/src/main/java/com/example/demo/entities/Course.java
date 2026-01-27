package com.example.demo.entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;


@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<Topic> topics;


    protected Course() {}

    public Course(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public void setTopics(Set<Topic> topics) {
    this.topics = topics;
}


    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Set<Topic> getTopics() {
    return topics;
}

}
