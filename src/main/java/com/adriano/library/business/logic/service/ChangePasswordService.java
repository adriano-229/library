package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ChangePasswordService {

    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public boolean matches(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public boolean isEncoded(String password) {
        // BCrypt encoded passwords start with $2a$, $2b$, or $2y$
        return password != null && password.startsWith("$");
    }

    public boolean isNotEncoded(String password) {
        return !isEncoded(password);
    }
}
