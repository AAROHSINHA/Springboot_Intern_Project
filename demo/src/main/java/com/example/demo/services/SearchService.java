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

        String trimmedQuery = query.trim();

        // 1. Try Baseline Search (ILIKE for partial matches)
        List<SubtopicRepository.SearchProjection> flatResults = subtopicRepository.performFullTextSearch(trimmedQuery);

        // 2. Fallback to Fuzzy Search ONLY if baseline returns nothing
        if (flatResults.isEmpty()) {
            flatResults = subtopicRepository.performFuzzySearch(trimmedQuery);
        }

        // 3. Group by Course ID to create the nested structure (Preserves Baseline Format)
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
                courseMap.put("courseTitle", matches.get(0).getCourseTitle());
                courseMap.put("matches", matchList);

                return courseMap;
            })
            .toList();

        // 4. Final Response Construction
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("query", query);
        response.put("results", groupedResults);

        return response;
    }
}