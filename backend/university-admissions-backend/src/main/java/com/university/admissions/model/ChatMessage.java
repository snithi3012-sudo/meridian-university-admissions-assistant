package com.university.admissions.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * A single question-answer exchange, saved so a student can look back
 * at their past conversations with the chatbot.
 */
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String userEmail;
    private String question;
    private String answer;
    private LocalDateTime timestamp;

    public ChatMessage() {
        // Required by Spring Data MongoDB
    }

    public ChatMessage(String userEmail, String question, String answer, LocalDateTime timestamp) {
        this.userEmail = userEmail;
        this.question = question;
        this.answer = answer;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
