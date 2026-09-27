package com.university.admissions.service;

import com.university.admissions.ai.GeminiService;
import com.university.admissions.ai.RagService;
import com.university.admissions.model.ChatMessage;
import com.university.admissions.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Sits between ChatController and the AI layer.
 * Phase 6: builds a RAG-augmented prompt before calling Gemini.
 * Phase 7: also saves every question/answer pair to MongoDB, tied to
 * whichever user asked it, and can fetch that history back.
 */
@Service
public class ChatService {

    private final GeminiService geminiService;
    private final RagService ragService;
    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(GeminiService geminiService, RagService ragService, ChatRepository chatRepository) {
        this.geminiService = geminiService;
        this.ragService = ragService;
        this.chatRepository = chatRepository;
    }

    public String getAnswer(String userEmail, String question) {
        String augmentedPrompt = ragService.buildPrompt(question);
        String answer = geminiService.generateAnswer(augmentedPrompt);

        ChatMessage message = new ChatMessage(userEmail, question, answer, LocalDateTime.now());
        chatRepository.save(message);

        return answer;
    }

    public List<ChatMessage> getHistory(String userEmail) {
        return chatRepository.findByUserEmailOrderByTimestampDesc(userEmail);
    }
}
