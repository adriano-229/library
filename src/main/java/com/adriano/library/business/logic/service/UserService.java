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
    private final ChangePasswordService changePasswordService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, ChangePasswordService changePasswordService) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.changePasswordService = changePasswordService;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Hook: before creating a user, encode password if present and not already encoded
    @Override
    public void beforeSave(User entity) {
        if (entity.getPassword() != null && !entity.getPassword().isEmpty() && changePasswordService.isNotEncoded(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
    }

    // Hook: before updating, if password blank keep existing; otherwise encode if needed
    @Override
    public void beforeUpdate(Long id, User entity) {
        if (entity.getPassword() == null || entity.getPassword().isEmpty()) {
            userRepository.findById(id).ifPresent(existing -> entity.setPassword(existing.getPassword()));
        } else if (changePasswordService.isNotEncoded(entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        }
    }

    // Hook: after reading a user, clear password field
    @Override
    public void afterRead(User entity) {
        entity.setPassword("");
    }
}
