package com.university.admissions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the AI University Admissions Assistant backend.
 *
 * Phase 1 goal: prove the Spring Boot app boots and serves a REST endpoint.
 * Nothing else (MongoDB, Gemini, RAG, JWT) is wired up yet — that comes
 * in later phases, one at a time.
 */
@SpringBootApplication
public class UniversityAdmissionsApplication {
    

    public static void main(String[] args) {
        SpringApplication.run(UniversityAdmissionsApplication.class, args);
    }
}
