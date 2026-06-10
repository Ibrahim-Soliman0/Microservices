package com.example.oauth2.controller;

import com.example.oauth2.token.TokenGenerator;
import com.example.oauth2.user.UserClient;
import com.example.oauth2.user.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserClient userClient;
    private final TokenGenerator tokenGenerator;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            HttpSession session,
            @AuthenticationPrincipal OAuth2User oAuth2User) {

        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Create or find user in user-service
        UserResponse user = userClient.createOrUpdateUser(
                email, name, oAuth2User.getAttribute("picture"), "google");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to sync user"));
        }

        // Generate JWT token
        String token = tokenGenerator.generateToken(oAuth2User);

        // Store token in user-service
        userClient.saveToken(user.getId(), token, "no-refresh");

        // Store in session
        session.setAttribute("ACCESS_TOKEN", token);
        session.setAttribute("USER_EMAIL", email);
        session.setAttribute("USER_NAME", name);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", email,
                "name", name,
                "picture", oAuth2User.getAttribute("picture"),
                "token", token,
                "sessionId", session.getId()
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "No active session"));
        }

        String token = (String) session.getAttribute("ACCESS_TOKEN");
        String email = (String) session.getAttribute("USER_EMAIL");
        String name = (String) session.getAttribute("USER_NAME");

        if (token == null || email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "error", "Incomplete session"));
        }

        return ResponseEntity.ok(Map.of(
                "valid", true,
                "email", email,
                "name", name,
                "token", token
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("service", "auth-service", "status", "UP"));
    }
}
