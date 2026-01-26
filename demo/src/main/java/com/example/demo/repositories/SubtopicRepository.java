package com.example.demo.repositories;

import com.example.demo.entities.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
    boolean existsById(String id);
}
