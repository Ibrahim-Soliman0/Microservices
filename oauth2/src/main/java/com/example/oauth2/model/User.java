package com.example.oauth2.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String picture;

    @Column(unique = true)
    private String googleId;


    @Column(nullable = false)
    private String provider;

    @Builder.Default
    private boolean isNewUser = false;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;


    @PrePersist
    public void onFirstSave() {
        this.createdAt   = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.lastLoginAt = LocalDateTime.now();
        this.isNewUser   = false;
    }
}
