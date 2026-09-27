package com.university.admissions.controller;

import com.university.admissions.model.Course;
import com.university.admissions.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Exposes CRUD endpoints for managing courses.
 *
 * POST   /api/courses        - create a course
 * GET    /api/courses        - list all courses
 * GET    /api/courses/{id}   - get one course
 * PUT    /api/courses/{id}   - update a course
 * DELETE /api/courses/{id}   - delete a course
 *
 * A proper global exception handler comes in Phase 12 - for now we
 * catch "not found" here so the API returns a clean 404 instead of a 500.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = courseService.createCourse(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(courseService.getCourseById(id));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Course course) {
        try {
            return ResponseEntity.ok(courseService.updateCourse(id, course));
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        try {
            courseService.deleteCourse(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundBody(ex.getMessage()));
        }
    }

    private Object notFoundBody(String message) {
        return java.util.Map.of(
                "success", false,
                "message", message,
                "status", 404
        );
    }
}
