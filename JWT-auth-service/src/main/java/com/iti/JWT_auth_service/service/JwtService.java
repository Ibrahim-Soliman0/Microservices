package com.iti.JWT_auth_service.service;

import com.iti.JWT_auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Autowired
    private JwtUtil jwtUtil;

    public String generateToken( Long userId, String username, String role) {
        return jwtUtil.generateToken(userId , username, role);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    public Long extractUserId(String token) {
        return jwtUtil.extractUserId(token);
    }

}
