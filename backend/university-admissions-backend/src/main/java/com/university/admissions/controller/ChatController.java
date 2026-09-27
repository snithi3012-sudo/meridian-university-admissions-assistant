package com.university.admissions.controller;

import com.university.admissions.dto.ChatRequest;
import com.university.admissions.dto.ChatResponse;
import com.university.admissions.model.ChatMessage;
import com.university.admissions.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * POST /api/chat         - ask a question, get an AI answer (RAG-augmented)
 * GET  /api/chat/history - see your own past questions and answers
 *
 * Both require a logged-in user (student or admin) - see SecurityConfig's
 * anyRequest().authenticated() catch-all. Spring automatically injects the
 * Authentication for us based on the Bearer token in the request.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            String answer = chatService.getAnswer(userEmail, request.getQuestion());
            return ResponseEntity.ok(new ChatResponse(answer));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "success", false,
                    "message", "Could not get an answer from the AI service. " + ex.getMessage(),
                    "status", 502
            ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getHistory(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(chatService.getHistory(userEmail));
    }
}
