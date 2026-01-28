package com.example.demo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;
import java.util.HashMap;
@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> sayHello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello from Aaroh Sinha. This is a short about-me route.");
        response.put("portfoliolink", "https://aarohsinha.space/");
        response.put("resume", "https://drive.google.com/file/d/1TWxeR9HasbXkuptjduDGSbhvtVi4ahRb/view?usp=sharing");
        response.put("github", "https://github.com/AAROHSINHA");
        response.put("linkedin", "https://www.linkedin.com/in/aaroh-sinha-375a8a324/");

        return response;
    }
}
