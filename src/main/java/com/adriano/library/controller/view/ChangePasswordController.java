package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.ChangePasswordService;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.security.core.Authentication;
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
    private final ChangePasswordService changePasswordService;

    public ChangePasswordController(UserService userService, ChangePasswordService changePasswordService) {
        this.userService = userService;
        this.changePasswordService = changePasswordService;
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

        // Validate current password
        if (!changePasswordService.matches(user, currentPassword)) {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
            return "redirect:/change-password";
        }

        // Validate new password matches confirmation
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match");
            return "redirect:/change-password";
        }

        // Validate password length
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 6 characters long");
            return "redirect:/change-password";
        }

        // Set new password; hooks will encode on update
        user.setPassword(newPassword);
        userService.update(user.getId(), user);

        redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        return "redirect:/change-password";
    }
}
