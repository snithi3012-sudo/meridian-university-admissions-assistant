package com.university.admissions.controller;

import com.university.admissions.dto.AuthResponse;
import com.university.admissions.dto.LoginRequest;
import com.university.admissions.dto.RegisterRequest;
import com.university.admissions.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * POST /api/auth/register - create a STUDENT (or, with the right secret, ADMIN) account
 * POST /api/auth/login    - exchange email+password for a JWT
 *
 * Both endpoints are public (see SecurityConfig) - you don't need a token
 * to call them, since that's how you get a token in the first place.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", ex.getMessage(), "status", 400));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", ex.getMessage(), "status", 401));
        }
    }
}
