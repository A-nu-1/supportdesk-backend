package com.supportdesk.security;

import com.supportdesk.user.Role;
import com.supportdesk.user.User;
import com.supportdesk.user.UserRepository;
import com.supportdesk.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;

        public AuthService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
        }

        public UserResponse register(RegisterRequest request) {

                if (userRepository.existsByEmail(request.email())) {
                        throw new IllegalArgumentException(
                                        "A user with this email already exists.");
                }

                User user = new User();

                user.setEmail(request.email());
                user.setPasswordHash(
                                passwordEncoder.encode(request.password()));
                user.setFirstName(request.firstName());
                user.setLastName(request.lastName());
                user.setRole(Role.EMPLOYEE);
                user.setActive(true);

                OffsetDateTime now = OffsetDateTime.now();

                user.setCreatedAt(now);
                user.setUpdatedAt(now);

                User savedUser = userRepository.save(user);

                return UserResponse.from(savedUser);
        }

        public LoginResponse login(LoginRequest request) {

                User user = userRepository.findByEmail(request.email())
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Invalid email or password."));

                if (!user.isActive()) {
                        throw new IllegalArgumentException(
                                        "This user account is inactive.");
                }

                if (!passwordEncoder.matches(
                                request.password(),
                                user.getPasswordHash())) {
                        throw new IllegalArgumentException(
                                        "Invalid email or password.");
                }

                String token = jwtService.generateToken(user);

                return new LoginResponse(
                                token,
                                UserResponse.from(user), false);
        }

        public LoginResponse demoLogin(
                        DemoLoginRequest request) {

                String email = switch (request.role()) {
                        case EMPLOYEE ->
                                "employee@example.com";

                        case SUPPORT_AGENT ->
                                "agent@example.com";

                        case ADMIN ->
                                "anu@example.com";
                };

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalStateException(
                                                "Demo user is not configured."));

                if (!user.isActive()) {
                        throw new IllegalStateException(
                                        "Demo user is inactive.");
                }

                String token = jwtService.generateDemoToken(user);

                return new LoginResponse(
                                token,
                                UserResponse.from(user),
                                true);
        }
}