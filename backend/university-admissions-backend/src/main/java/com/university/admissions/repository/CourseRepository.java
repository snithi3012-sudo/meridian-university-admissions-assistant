package com.university.admissions.repository;

import com.university.admissions.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Data access layer for Course documents.
 *
 * Extending MongoRepository gives us save(), findAll(), findById(),
 * deleteById(), etc. for free - no implementation needed.
 */
@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    // Custom query methods can be added here later, e.g.:
    // List<Course> findByCourseNameContainingIgnoreCase(String keyword);
}
