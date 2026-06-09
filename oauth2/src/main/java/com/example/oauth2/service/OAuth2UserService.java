package com.example.oauth2.service;




import com.example.oauth2.model.User;
import com.example.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Called automatically by Spring Security after Google login succeeds.
 *
 * Responsibilities:
 *  1. Extract user data from Google's response
 *  2. Check if user already exists in DB
 *  3. Save new users / update existing users
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();


        String googleId = (String) attributes.get("sub");
        String email    = (String) attributes.get("email");
        String name     = (String) attributes.get("name");
        String picture  = (String) attributes.get("picture");

        log.debug("Google OAuth2 login attempt for email: {}", email);

        boolean isNewUser = !userRepository.existsByEmail(email);

        if (isNewUser) {
            log.info("New user registering via Google: {}", email);

            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .picture(picture)
                    .googleId(googleId)
                    .provider("google")
                    .isNewUser(true)
                    .build();

            userRepository.save(newUser);
            log.info("User saved to DB: id={}, email={}", newUser.getId(), email);

        } else {
            log.info("Existing user logging in: {}", email);

            User existing = userRepository.findByEmail(email).get();
            existing.setName(name);
            existing.setPicture(picture);
            userRepository.save(existing);
        }
        return oAuth2User;
    }
}
