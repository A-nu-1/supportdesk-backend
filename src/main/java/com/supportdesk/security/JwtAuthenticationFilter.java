package com.supportdesk.security;

import com.supportdesk.user.Role;
import com.supportdesk.user.User;
import com.supportdesk.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String email =
                jwtService.extractEmail(token);

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user != null && user.isActive()) {

            List<SimpleGrantedAuthority>
                    authorities =
                    new ArrayList<>();

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_"
                                    + user.getRole().name()
                    )
            );

            boolean demo =
                    jwtService.isDemoToken(token);

            if (user.getRole() == Role.ADMIN
                    && !demo) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                "REAL_ADMIN"
                        )
                );
            }

            UsernamePasswordAuthenticationToken
                    authentication =
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            null,
                            authorities
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication
                    );
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}