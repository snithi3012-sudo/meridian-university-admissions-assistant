package com.university.admissions.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a single course offered by the university.
 * Maps to a document in the "courses" collection in MongoDB.
 *
 * Example document:
 * {
 *   "courseName": "B.E. Computer Science and Engineering",
 *   "duration": "4 Years",
 *   "eligibility": "12th standard with Mathematics",
 *   "fees": 85000,
 *   "availableSeats": 120
 * }
 */
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    private String courseName;
    private String duration;
    private String eligibility;
    private double fees;
    private int availableSeats;

    public Course() {
        // Required by Spring Data MongoDB for object mapping
    }

    public Course(String courseName, String duration, String eligibility, double fees, int availableSeats) {
        this.courseName = courseName;
        this.duration = duration;
        this.eligibility = eligibility;
        this.fees = fees;
        this.availableSeats = availableSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public double getFees() {
        return fees;
    }

    public void setFees(double fees) {
        this.fees = fees;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
