package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ChangePasswordController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/change-password")
    public String showForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<User> user = userService.findByEmail(email);

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "users/change-password";
        }
        return "redirect:/login";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/change-password";
        }
        User user = userOpt.get();


        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Incorrect password");
            return "redirect:/change-password";
        }


        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/change-password";
        }

        // encrypt and save new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        redirectAttributes.addFlashAttribute("success", "Password set successfully");
        return "redirect:/";
    }
}
