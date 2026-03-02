package org.example.lesson1First.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiConfigController {
    @GetMapping("/test")
    public String test() {
        return "API is working!";
    }

    @GetMapping("/endpoints")
    public List<Map<String, String>> getEndpoints() {
        return List.of(
                Map.of("url", "/api/users", "method", "GET", "description", "Get all users"),
                Map.of("url", "/api/users/{id}", "method", "GET", "description", "Get user by id")
                // Добавьте ваши реальные endpoints здесь
        );
    }
}
