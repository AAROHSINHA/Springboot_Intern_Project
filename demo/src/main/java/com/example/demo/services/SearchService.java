package com.example.demo.services;

import com.example.demo.repositories.SubtopicRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final SubtopicRepository subtopicRepository;

    public SearchService(SubtopicRepository subtopicRepository) {
        this.subtopicRepository = subtopicRepository;
    }

    public Map<String, Object> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Map.of("query", "", "results", List.of());
        }

        // 1. Get flat data from DB (Projected Interface)
        List<SubtopicRepository.SearchProjection> flatResults = subtopicRepository.performFullTextSearch(query.trim());

        // 2. Group by Course ID to create the nested structure
        List<Map<String, Object>> groupedResults = flatResults.stream()
            .collect(Collectors.groupingBy(
                SubtopicRepository.SearchProjection::getCourseId,
                LinkedHashMap::new, // Keep insertion order
                Collectors.toList()
            ))
            .entrySet().stream()
            .map(entry -> {
                String courseId = entry.getKey();
                List<SubtopicRepository.SearchProjection> matches = entry.getValue();

                // Build the list of matches for this course
                List<Map<String, Object>> matchList = matches.stream()
                    .map(m -> {
                        Map<String, Object> matchMap = new LinkedHashMap<>();
                        matchMap.put("type", m.getMatchType());
                        matchMap.put("topicTitle", m.getTopicTitle());
                        matchMap.put("subtopicId", m.getSubtopicId());
                        matchMap.put("subtopicTitle", m.getSubtopicTitle());
                        matchMap.put("snippet", m.getSnippet());
                        return matchMap;
                    })
                    .toList();

                // Build the Course Object
                Map<String, Object> courseMap = new LinkedHashMap<>();
                courseMap.put("courseId", courseId);
                // We take the course title from the first match (it's the same for all in this group)
                courseMap.put("courseTitle", matches.get(0).getCourseTitle());
                courseMap.put("matches", matchList);

                return courseMap;
            })
            .toList();

        // 3. Final Response Construction
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("query", query);
        response.put("results", groupedResults);

        return response;
    }
}