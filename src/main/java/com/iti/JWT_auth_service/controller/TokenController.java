package com.iti.JWT_auth_service.controller;

import com.iti.JWT_auth_service.entity.RefreshToken;
import com.iti.JWT_auth_service.model.RefreshTokenRequest;
import com.iti.JWT_auth_service.model.TokenRequest;
import com.iti.JWT_auth_service.model.TokenResponse;
import com.iti.JWT_auth_service.service.JwtService;
import com.iti.JWT_auth_service.service.RefreshTokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
public class TokenController {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public TokenController(
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/generate")
    public TokenResponse generate(@RequestBody TokenRequest request) {

        String accessToken = jwtService.generateToken(
                request.getUserId(),
                request.getUsername(),
                request.getRole()
        );

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        request.getUserId()
                );

        return new TokenResponse(
                accessToken,
                refreshToken.getToken()
        );
    }

    @PostMapping("/validate")
    public boolean validate(@RequestParam String token) {
        return jwtService.validateToken(token);
    }

    @GetMapping("/extract/username")
    public String extractUsername(@RequestParam String token) {
        return jwtService.extractUsername(token);
    }

    @GetMapping("/extract/userId")
    public Long extractUserId(@RequestParam String token) {
        return jwtService.extractUserId(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(
            @RequestBody RefreshTokenRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(
                        request.getRefreshToken()
                );

        String newAccessToken =
                jwtService.generateToken(
                        refreshToken.getUserId(),
                        "user",
                        "USER"
                );

        return new TokenResponse(
                newAccessToken,
                refreshToken.getToken()
        );
    }

    @PostMapping("/logout")
    public String logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.revokeToken(
                request.getRefreshToken()
        );

        return "Refresh token revoked successfully";
    }
}