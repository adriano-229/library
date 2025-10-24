package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    @PostMapping
    public String save(@ModelAttribute User user) {
        // if the user already exists and the password field is empty, keep the existing password
        if (user.getId() != null && (user.getPassword() == null || user.getPassword().isEmpty())) {
            User existingUser = userService.findById(user.getId()).orElseThrow();
            user.setPassword(existingUser.getPassword()); // keep existing password
        } else if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // encrypt the password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        // clear the password field before sending to the view
        user.setPassword("");
        model.addAttribute("user", user);
        return "users/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
