package com.example.demo.dto;

import java.util.List;

public class EnrollmentProgressResponse {

    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private long totalSubtopics;
    private long completedSubtopics;
    private double completionPercentage;
    private List<CompletedSubtopicDTO> completedItems;

    public EnrollmentProgressResponse(
            Long enrollmentId,
            String courseId,
            String courseTitle,
            long totalSubtopics,
            long completedSubtopics,
            double completionPercentage,
            List<CompletedSubtopicDTO> completedItems
    ) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.totalSubtopics = totalSubtopics;
        this.completedSubtopics = completedSubtopics;
        this.completionPercentage = completionPercentage;
        this.completedItems = completedItems;
    }

    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public long getTotalSubtopics() {
        return totalSubtopics;
    }

    public long getCompletedSubtopics() {
        return completedSubtopics;
    }

    public double getCompletionPercentage() {
        return completionPercentage;
    }

    public List<CompletedSubtopicDTO> getCompletedItems() {
        return completedItems;
    }
}

