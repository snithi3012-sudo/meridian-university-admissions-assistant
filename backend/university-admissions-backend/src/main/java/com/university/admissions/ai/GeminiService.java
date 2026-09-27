package com.university.admissions.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Talks directly to Google's Gemini API.
 *
 * This is intentionally the ONLY class that knows about Gemini's request/response
 * shape. If Google changes their API, or we swap providers later, only this
 * file needs to change - ChatService and ChatController don't care how the
 * answer was generated.
 */
@Service
public class GeminiService {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.api.base-url}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Sends a plain question to Gemini and returns its plain-text answer.
     * Phase 5 sends the question as-is. From Phase 6 (RAG) onward, the
     * caller will build a richer prompt that includes retrieved
     * knowledge-base context before it ever reaches this method.
     */
    public String generateAnswer(String promptText) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", promptText)
                        ))
                )
        );

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/{model}:generateContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractAnswerText(response);

        } catch (Exception ex) {
            // Don't leak API keys or raw stack traces to the student-facing chat.
            throw new RuntimeException("Gemini request failed: " + ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractAnswerText(Map<String, Object> response) {
        if (response == null) {
            throw new RuntimeException("Empty response from Gemini");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini returned no candidates. Full response: " + response);
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("Gemini candidate had no text parts. Full response: " + response);
        }

        return (String) parts.get(0).get("text");
    }
}
