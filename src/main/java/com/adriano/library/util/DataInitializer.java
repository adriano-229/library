package com.adriano.library.util;

import com.adriano.library.business.logic.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    public DataInitializer(UserService userService) {
    }

    @Override
    public void run(String... args) {
//        User admin = new User();
//        admin.setEmail("admin@email.com");
//        admin.setPassword("admin123");
//        admin.setRole(Role.ADMIN);
//        userService.save(admin);
//
//        User user = new User();
//        user.setEmail("user@email.com");
//        user.setPassword("user123");
//        user.setRole(Role.USER);
//        userService.save(user);
    }
}
