package com.iti.jets.user.dto;

import com.iti.jets.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    int id;
    String name;
    String email;

    public static UserResponse from(User user) {
        return UserResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail()).build();
    }
}
