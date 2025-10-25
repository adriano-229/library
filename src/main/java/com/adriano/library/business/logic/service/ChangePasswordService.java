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

    boolean isEncoded(String password) {
        return password == null || !password.startsWith("$"); // simplistic check (e.g., BCrypt)
    }
}
