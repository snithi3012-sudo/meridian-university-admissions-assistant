package com.university.admissions.service;

import com.university.admissions.dto.AuthResponse;
import com.university.admissions.dto.LoginRequest;
import com.university.admissions.dto.RegisterRequest;
import com.university.admissions.model.User;
import com.university.admissions.repository.UserRepository;
import com.university.admissions.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${admin.registration.secret:change-this-admin-secret}")
    private String adminRegistrationSecret;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String role = resolveRole(request);

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                role
        );
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole());
    }

    /**
     * Defaults every new account to STUDENT. The only way to become ADMIN
     * through this endpoint is to explicitly request role "ADMIN" AND supply
     * the correct adminSecret (set via admin.registration.secret in
     * application.properties). This stops random users from granting
     * themselves admin just by sending {"role": "ADMIN"}.
     */
    private String resolveRole(RegisterRequest request) {
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            if (adminRegistrationSecret.equals(request.getAdminSecret())) {
                return "ADMIN";
            }
            throw new IllegalArgumentException("Invalid admin secret");
        }
        return "STUDENT";
    }
}
