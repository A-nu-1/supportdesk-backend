package com.supportdesk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/demo-login",
                                                                "/error",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs.yaml",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.POST,
                                                                "/api/categories")
                                                .hasAuthority("REAL_ADMIN")

                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.POST,
                                                                "/api/tickets/*/claim")
                                                .hasRole("SUPPORT_AGENT")

                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.GET,
                                                                "/api/tickets/assigned-to-me")
                                                .hasRole("SUPPORT_AGENT")
                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.PATCH,
                                                                "/api/tickets/*/status")
                                                .hasAnyRole("SUPPORT_AGENT", "ADMIN")

                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.PATCH,
                                                                "/api/tickets/*/priority")
                                                .hasAnyRole("SUPPORT_AGENT", "ADMIN")

                                                .requestMatchers(
                                                                "/api/users",
                                                                "/api/users/**")
                                                .hasAuthority("REAL_ADMIN")

                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.PATCH,
                                                                "/api/categories/*")
                                                .hasAuthority("REAL_ADMIN")

                                                .anyRequest().authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}