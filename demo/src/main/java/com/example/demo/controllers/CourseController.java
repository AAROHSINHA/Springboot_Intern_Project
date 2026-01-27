package com.example.demo.controllers;
import com.example.demo.dto.CourseDetailDTO;
import com.example.demo.dto.CourseSummaryDTO;
import com.example.demo.entities.Course;
import com.example.demo.entities.Course;
import com.example.demo.services.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Map<String, List<CourseSummaryDTO>> getAllCourses() {
        return Map.of("courses", courseService.getCourseSummaries());
    }

   @GetMapping("/{id}")
public CourseDetailDTO getCourse(@PathVariable String id) {
    return courseService.getCourseDetailsById(id);
}

}
