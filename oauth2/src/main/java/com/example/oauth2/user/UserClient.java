package com.example.oauth2.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {

    private final RestTemplate restTemplate;

    public UserResponse createOrUpdateUser(String email, String name, String picture, String provider) {
        Map<String, String> body = Map.of(
                "email", email,
                "name", name != null ? name : "Unknown",
                "picture", picture != null ? picture : "",
                "provider", provider
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<UserResponse> response = restTemplate.postForEntity(
                    "http://user-service/api/users",
                    request,
                    UserResponse.class
            );
            log.info("User synced with user-service: {}", email);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to sync user with user-service", e);
            return null;
        }
    }

    public UserResponse getUserByEmail(String email) {
        try {
            ResponseEntity<UserResponse> response = restTemplate.getForEntity(
                    "http://user-service/api/users/email/" + email,
                    UserResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get user by email: {}", email, e);
            return null;
        }
    }

    public void saveToken(Long userId, String accessToken, String refreshToken) {
        Map<String, String> body = Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.put(
                    "http://user-service/api/users/" + userId + "/token",
                    request
            );
            log.info("Token saved for user id: {}", userId);
        } catch (Exception e) {
            log.error("Failed to save token for user id: {}", userId, e);
        }
    }
}
