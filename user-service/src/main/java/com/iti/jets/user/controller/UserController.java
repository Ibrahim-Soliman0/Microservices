package com.iti.jets.user.controller;

import com.iti.jets.user.dto.RegisterRequest;
import com.iti.jets.user.dto.TokenRequest;
import com.iti.jets.user.dto.UserResponse;
import com.iti.jets.user.dto.UserUpdateRequest;
import com.iti.jets.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createOrUpdate(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/users - email={}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createOrUpdate(request));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/users");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("PUT /api/users/{}", id);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PutMapping("/{id}/token")
    public ResponseEntity<UserResponse> saveToken(
            @PathVariable Long id,
            @RequestBody TokenRequest request) {
        log.info("PUT /api/users/{}/token", id);
        return ResponseEntity.ok(userService.saveToken(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
