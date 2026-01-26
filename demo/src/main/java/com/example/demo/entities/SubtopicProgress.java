package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "subtopic_progress",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "subtopic_id"})
    }
)
public class SubtopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtopic_id", nullable = false)
    private Subtopic subtopic;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected SubtopicProgress() {}

    public SubtopicProgress(User user, Subtopic subtopic) {
        this.user = user;
        this.subtopic = subtopic;
    }

    public void markCompleted() {
        this.completedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Instant getCompletedAt() { return completedAt; }
}
