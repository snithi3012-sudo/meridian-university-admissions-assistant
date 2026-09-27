package com.university.admissions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central security rules for the whole app.
 *
 * Public (no login needed):
 *   - /api/health
 *   - /api/auth/**              (register, login)
 *   - GET  /api/courses/**      (anyone can browse courses)
 *   - GET  /api/admissions/**   (anyone can browse admission info)
 *
 * Admin only:
 *   - POST/PUT/DELETE /api/courses/**
 *   - POST/PUT/DELETE /api/admissions/**
 *
 * Everything else requires at least a logged-in user (STUDENT or ADMIN).
 * This includes /api/chat and /api/chat/history.
 *
 * Phase 8: also configures CORS so the React frontend (running on a
 * different port, e.g. localhost:5173) is allowed to call this API from
 * the browser. Without this, the browser blocks the requests even though
 * Postman never had a problem (Postman isn't a browser, so it ignores CORS).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Add your deployed frontend's URL here too once you deploy it.
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/**", "/api/admissions/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/courses/**", "/api/admissions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/**", "/api/admissions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/**", "/api/admissions/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
