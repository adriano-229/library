package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends BaseService<User, Long> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Hook: before creating a user, encode password if present and not already encoded
    @Override
    public void beforeCreate(User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty() && !isEncoded(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
    }

    // Hook: before updating, if password blank keep existing; otherwise encode if needed
    @Override
    public void beforeUpdate(Long id, User entity) {
        if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
            userRepository.findById(id).ifPresent(existing -> entity.setPassword(existing.getPassword()));
        } else if (!isEncoded(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
    }

    public boolean matches(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private boolean isEncoded(String password) {
        return password != null && password.startsWith("$"); // simplistic check (e.g., BCrypt)
    }
}
