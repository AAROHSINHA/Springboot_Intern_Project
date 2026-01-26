package com.example.demo;

import com.example.demo.services.CourseService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseService courseService;

    public DataSeeder(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public void run(String... args) throws Exception {
        courseService.seedCoursesIfEmpty();
    }
}
