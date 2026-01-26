package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    public String testDbConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return (result != null && result == 1)
                    ? "Database is connected!"
                    : "Database connection failed.";
        } catch (Exception e) {
            return "❌ Cannot connect to DB: " + e.getMessage();
        }
    }
}
