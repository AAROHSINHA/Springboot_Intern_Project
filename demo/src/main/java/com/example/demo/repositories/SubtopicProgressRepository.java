package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entities.*;
import java.util.Optional;

public interface SubtopicProgressRepository extends JpaRepository<SubtopicProgress, Long> {

    Optional<SubtopicProgress> findByUserAndSubtopic(User user, Subtopic subtopic);
}
