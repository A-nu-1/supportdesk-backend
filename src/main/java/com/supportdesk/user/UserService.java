package com.supportdesk.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserResponse updateUser(
            Long userId,
            UpdateUserRequest request,
            String currentUserEmail) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new com.supportdesk.exception.ResourceNotFoundException(
                        "User was not found."));

        boolean updatingSelf = user.getEmail()
                .equalsIgnoreCase(
                        currentUserEmail);

        if (updatingSelf) {

            if (request.role() != Role.ADMIN) {
                throw new IllegalArgumentException(
                        "You cannot remove your own admin role.");
            }

            if (!request.active()) {
                throw new IllegalArgumentException(
                        "You cannot deactivate your own account.");
            }
        }

        user.setRole(request.role());
        user.setActive(request.active());
        user.setUpdatedAt(
                java.time.OffsetDateTime.now());

        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
}