package com.university.admissions.service;

import com.university.admissions.model.Course;
import com.university.admissions.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Business logic for courses. The controller talks to this service,
 * and this service talks to the repository. Keeping this layer separate
 * means we can add validation, rules, or logging here later without
 * touching the controller or the database code.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Course not found with id: " + id));
    }

    public Course updateCourse(String id, Course updatedCourse) {
        Course existing = getCourseById(id);

        existing.setCourseName(updatedCourse.getCourseName());
        existing.setDuration(updatedCourse.getDuration());
        existing.setEligibility(updatedCourse.getEligibility());
        existing.setFees(updatedCourse.getFees());
        existing.setAvailableSeats(updatedCourse.getAvailableSeats());

        return courseRepository.save(existing);
    }

    public void deleteCourse(String id) {
        if (!courseRepository.existsById(id)) {
            throw new NoSuchElementException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}
