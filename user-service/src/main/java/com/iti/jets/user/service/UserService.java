package com.iti.jets.user.service;

import com.iti.jets.user.dto.LoginRequest;
import com.iti.jets.user.dto.RegisterRequest;
import com.iti.jets.user.dto.UserResponse;
import com.iti.jets.user.dto.UserUpdateRequest;
import com.iti.jets.user.exception.EmailAlreadyExistsException;
import com.iti.jets.user.exception.InvalidCredentialsException;
import com.iti.jets.user.exception.UserNotFoundException;
import com.iti.jets.user.model.User;
import com.iti.jets.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);
        log.info("User registered successfully with id: {}", saved.getId());
        return UserResponse.from(saved);
    }


    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            throw new InvalidCredentialsException();
        }

        log.info("Login successful for user id: {}", user.getId());
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        log.info("Updating user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        User updated = userRepository.save(user);
        log.info("User id: {} updated successfully", id);
        return UserResponse.from(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("User id: {} deleted", id);
    }
}
