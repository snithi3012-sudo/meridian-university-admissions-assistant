package com.university.admissions.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a single piece of general admission information -
 * not tied to a specific course. For example:
 *
 * {
 *   "category": "eligibility",
 *   "title": "General Eligibility Criteria",
 *   "content": "Candidates must have completed 12th standard..."
 * }
 *
 * The "category" field is what the chatbot/RAG system (later phases)
 * and the /api/admissions/{category} endpoint will use to look things up.
 * Suggested categories: eligibility, fees, deadlines, documents,
 * process, scholarships, hostel, contact.
 */
@Document(collection = "admissions")
public class Admission {

    @Id
    private String id;

    private String category;
    private String title;
    private String content;

    public Admission() {
        // Required by Spring Data MongoDB
    }

    public Admission(String category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
