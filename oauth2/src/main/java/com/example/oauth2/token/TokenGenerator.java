package com.example.oauth2.token;

import com.example.oauth2.model.TokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenGenerator {

    private final RestTemplate restTemplate;

    public String generateToken(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Map<String, Object> requestPayload = Map.of(
                "userId", 0L,
                "username", name != null ? name : "Unknown",
                "role", "ROLE_USER"
        );

        try {
            log.info("Requesting token from JWT service for: {}", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    "http://jwt-auth-service/token/generate",
                    request,
                    TokenResponse.class
            );

            log.info("Successfully received token from JWT service.");
            return response.getBody() != null ? response.getBody().getToken() : "no-token";

        } catch (Exception e) {
            log.error("Failed to reach JWT service", e);
            return "fallback-token-service-down";
        }
    }
}
