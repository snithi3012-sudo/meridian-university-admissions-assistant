package com.university.admissions.ai;

import com.university.admissions.model.Admission;
import com.university.admissions.model.Course;
import com.university.admissions.repository.AdmissionRepository;
import com.university.admissions.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Retrieval-Augmented Generation (RAG) - the "R" and "A" before Gemini's "G".
 *
 * Flow:
 *   1. Take the student's question
 *   2. Pull out meaningful keywords (ignore filler words like "what", "is", "the")
 *   3. Search our OWN MongoDB data (courses + admissions) for anything matching
 *   4. If we find relevant records, hand them to Gemini as context and ask
 *      it to answer USING that data
 *   5. If we find nothing relevant, fall back to a plain question so Gemini
 *      still tries to help using its general knowledge
 *
 * This is a simple, keyword-based retrieval step - good enough for a
 * small knowledge base like ours. A real production RAG system would use
 * embeddings + a vector database instead of keyword matching (that upgrade
 * is a natural "Phase 7" if you want to go further later).
 */
@Service
public class RagService {

    private final CourseRepository courseRepository;
    private final AdmissionRepository admissionRepository;

    // Common words that don't help us find relevant records - filtering
    // these out means "What is the fee for CSE?" effectively searches for
    // just "fee" and "cse".
    private static final Set<String> STOP_WORDS = Set.of(
            "what", "is", "the", "for", "a", "an", "of", "in", "on", "and",
            "to", "are", "how", "much", "does", "do", "i", "my", "you",
            "your", "please", "can", "tell", "me", "about", "university",
            "course", "admission", "it", "be", "this", "that", "with"
    );

    @Autowired
    public RagService(CourseRepository courseRepository, AdmissionRepository admissionRepository) {
        this.courseRepository = courseRepository;
        this.admissionRepository = admissionRepository;
    }

    /**
     * Builds the final prompt to send to Gemini: official data (if any was
     * found) plus clear instructions, followed by the student's question.
     */
    public String buildPrompt(String question) {
        String context = retrieveContext(question);

        if (context.isBlank()) {
            // Nothing relevant in our knowledge base - let Gemini answer
            // from general knowledge, same as Phase 5.
            return question;
        }

        return """
                You are an admissions assistant for a university. Answer the student's \
                question using ONLY the official university information below when it's \
                relevant. If the information below doesn't fully cover the question, you \
                may add brief general knowledge, but clearly say when you're doing so.

                OFFICIAL UNIVERSITY INFORMATION:
                %s

                STUDENT QUESTION:
                %s

                ANSWER:
                """.formatted(context, question);
    }

    private String retrieveContext(String question) {
        Set<String> keywords = extractKeywords(question);
        if (keywords.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();

        for (Course course : courseRepository.findAll()) {
            String searchable = (safe(course.getCourseName()) + " " + safe(course.getEligibility())).toLowerCase();
            if (containsAnyKeyword(searchable, keywords)) {
                context.append("- Course: ").append(course.getCourseName())
                        .append(" | Duration: ").append(course.getDuration())
                        .append(" | Eligibility: ").append(course.getEligibility())
                        .append(" | Fees: ").append(course.getFees())
                        .append(" | Available Seats: ").append(course.getAvailableSeats())
                        .append("\n");
            }
        }

        for (Admission admission : admissionRepository.findAll()) {
            String searchable = (safe(admission.getCategory()) + " " + safe(admission.getTitle()) + " " + safe(admission.getContent())).toLowerCase();
            if (containsAnyKeyword(searchable, keywords)) {
                context.append("- [").append(admission.getCategory()).append("] ")
                        .append(admission.getTitle())
                        .append(": ").append(admission.getContent())
                        .append("\n");
            }
        }

        return context.toString();
    }

    private Set<String> extractKeywords(String question) {
        return Arrays.stream(question.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+"))
                .filter(word -> !word.isBlank())
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toSet());
    }

    private boolean containsAnyKeyword(String searchableText, Set<String> keywords) {
        return keywords.stream().anyMatch(searchableText::contains);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
