package com.example.oauth2.token;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenGenerator {

    private final RestClient restClient;

    public String generateToken(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

       // TODO : how JWT takes data
        Map<String, String> requestPayload = Map.of(
                "email", email,
                "name", name != null ? name : "Unknown",
                "role", "ROLE_USER"
        );

        try {
            log.info("Requesting token from external Token Service for: {}", email);

            // TODO the service url
            String targetUrl = "http://localhost:8082/api/tokens/generate";


            String tokenResponse = restClient.post()
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(String.class);

            log.info("Successfully received token from external service.");
            return tokenResponse;

        } catch (Exception e) {
            log.error("Failed to reach Token Service. Is your teammate's service running?", e);
            return "fallback-token-service-down";
        }
    }
}