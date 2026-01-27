package com.example.demo.dto;

import java.util.List;
import java.util.ArrayList;

// Top-level result per course
public class CourseSearchResult {
    private String courseId;
    private String courseTitle;
    private List<MatchDetail> matches;

    public CourseSearchResult(String courseId, String courseTitle) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.matches = new ArrayList<>();
    }

    public void addMatch(MatchDetail match) {
        matches.add(match);
    }

    // getters
    public String getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public List<MatchDetail> getMatches() { return matches; }
}

