package com.iti.jets.user.dto;

import lombok.Data;

@Data
public class TokenRequest {
    private String accessToken;
    private String refreshToken;
}
