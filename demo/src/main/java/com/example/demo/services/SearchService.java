package com.example.demo.services;

import com.example.demo.repositories.SubtopicRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final SubtopicRepository subtopicRepository;

    public SearchService(SubtopicRepository subtopicRepository) {
        this.subtopicRepository = subtopicRepository;
    }

    public Map<String, Object> search(String query) {
        List<SubtopicRepository.SearchProjection> flatResults = subtopicRepository.performFullTextSearch(query);

        List<Map<String, Object>> results = flatResults.stream()
            .collect(Collectors.groupingBy(
                SubtopicRepository.SearchProjection::getCourseId,
                LinkedHashMap::new,
                Collectors.toList()
            ))
            .entrySet().stream()
            .map(entry -> {
                var matches = entry.getValue();
                var first = matches.get(0);
                
                return Map.of(
                    "courseId", entry.getKey(),
                    "courseTitle", first.getCourseTitle(),
                    "matches", matches.stream().map(m -> Map.of(
                        "type", m.getMatchType(),
                        "topicTitle", m.getTopicTitle(),
                        "subtopicId", m.getSubtopicId(),
                        "subtopicTitle", m.getSubtopicTitle(),
                        "snippet", m.getSnippet() != null ? m.getSnippet() : ""
                    )).toList()
                );
            }).collect(Collectors.toList());

        return Map.of(
            "query", query,
            "results", results
        );
    }
}