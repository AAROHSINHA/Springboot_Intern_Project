package com.example.demo.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
}

)
public class User {

    @Id
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    protected User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = Instant.now();
    }


    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Instant getCreatedAt() { return createdAt; }
}
