package com.example.oauth2.controller;


import com.example.oauth2.model.User;
import com.example.oauth2.repository.UserRepository;
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

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            HttpSession session,
            @AuthenticationPrincipal OAuth2User oAuth2User) {

        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not logged in"));
        }

        String email   = oAuth2User.getAttribute("email");
        String token   = (String) session.getAttribute("ACCESS_TOKEN");

        User user = userRepository.findByEmail(email).orElse(null);

        return ResponseEntity.ok(Map.of(
                "email",       email,
                "name",        oAuth2User.getAttribute("name"),
                "picture",     oAuth2User.getAttribute("picture"),
                "token",       token != null ? token : "token-not-generated-yet",
                "isNewUser",   user != null && user.isNewUser(),
                "createdAt",   user != null ? user.getCreatedAt().toString() : "unknown",
                "sessionId",   session.getId()
        ));
    }



    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            log.warn("Validate called with no session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "error", "No active session. User must login first."
                    ));
        }

        String token = (String) session.getAttribute("ACCESS_TOKEN");
        String email = (String) session.getAttribute("USER_EMAIL");
        String name  = (String) session.getAttribute("USER_NAME");

        if (token == null || email == null) {
            log.warn("Session exists but has no token/email: sessionId={}", session.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "valid", false,
                            "error", "Session is incomplete. Please login again."
                    ));
        }

        log.debug("Session validated for user: {}", email);


        return ResponseEntity.ok(Map.of(
                "valid",  true,
                "email",  email,
                "name",   name,
                "token",  token
        ));
    }


    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {

        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "id",          user.getId(),
                        "email",       user.getEmail(),
                        "name",        user.getName(),
                        "picture",     user.getPicture(),
                        "provider",    user.getProvider(),
                        "createdAt",   user.getCreatedAt().toString(),
                        "lastLoginAt", user.getLastLoginAt().toString()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found: " + email)));
    }


    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "service", "auth-service",
                "status",  "UP"
        ));
    }
}
