package com.example.oauth2.config;

import com.example.oauth2.service.OAuth2UserService;
import com.example.oauth2.token.TokenGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final TokenGenerator tokenGenerator;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/oauth2/**",
                                "/api/auth/health"
                        ).permitAll()
                        .anyRequest().authenticated()   // everything else needs login
                )

                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(oAuth2UserService))
                        .successHandler(oauth2SuccessHandler())
                        .failureUrl("/login?error=true")
                )


                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )


                .csrf(csrf -> csrf.disable())
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }


    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Authentication authentication) throws IOException {

                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                String email = oAuth2User.getAttribute("email");
                String name  = oAuth2User.getAttribute("name");


                String token = tokenGenerator.generateToken(oAuth2User);
                log.info("Token generated for user: {}", email);

                HttpSession session = request.getSession(true);
                session.setAttribute("ACCESS_TOKEN", token);
                session.setAttribute("USER_EMAIL",   email);
                session.setAttribute("USER_NAME",    name);

                log.debug("Session created: id={}, email={}", session.getId(), email);

                response.sendRedirect("/api/auth/me");
            }
        };
    }

}
