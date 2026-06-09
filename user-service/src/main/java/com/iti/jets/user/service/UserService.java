package com.iti.jets.user.service;

import com.iti.jets.user.dto.RegisterRequest;
import com.iti.jets.user.dto.TokenRequest;
import com.iti.jets.user.dto.UserResponse;
import com.iti.jets.user.dto.UserUpdateRequest;
import com.iti.jets.user.exception.UserNotFoundException;
import com.iti.jets.user.model.User;
import com.iti.jets.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createOrUpdate(RegisterRequest request) {
        log.info("Creating/updating user with email: {}", request.getEmail());

        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseGet(() -> User.builder()
                        .email(request.getEmail())
                        .name(request.getName())
                        .picture(request.getPicture())
                        .provider(request.getProvider())
                        .build());

        user.setName(request.getName());
        if (request.getPicture() != null) user.setPicture(request.getPicture());
        if (request.getProvider() != null) user.setProvider(request.getProvider());

        User saved = userRepository.save(user);
        log.info("User saved with id: {}", saved.getId());
        return UserResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        User updated = userRepository.save(user);
        return UserResponse.from(updated);
    }

    @Transactional
    public UserResponse saveToken(Long id, TokenRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setAccessToken(request.getAccessToken());
        user.setRefreshToken(request.getRefreshToken());

        User saved = userRepository.save(user);
        log.info("Token saved for user id: {}", id);
        return UserResponse.from(saved);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User id: {} deleted", id);
    }
}
