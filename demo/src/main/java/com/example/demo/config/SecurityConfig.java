package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

   @Bean
public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/swagger-resources/**",
    "/webjars/**",

    "/api/auth/**",
    "/api/courses/**",
    "/api/search/**",

    "/hello"
).permitAll()

            // 2. Allow your login and register routes
            .requestMatchers("/api/auth/**").permitAll()
            
            // 3. Allow your public search/course routes
            .requestMatchers("/api/search/**", "/api/courses/**").permitAll()
            
            // 4. Everything else (like Enrollment) stays locked
            .anyRequest().authenticated()
        )
        // Add your filter to handle the tokens
        .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
}