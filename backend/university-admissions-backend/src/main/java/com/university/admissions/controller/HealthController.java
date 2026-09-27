package com.university.admissions.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple health-check endpoint so we can confirm the backend is alive
 * before wiring up MongoDB, Gemini, or anything else.
 *
 * GET /api/health -> { "message": "University Admissions Backend is running!" }
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("message", "University Admissions Backend is running!");
    }
}
