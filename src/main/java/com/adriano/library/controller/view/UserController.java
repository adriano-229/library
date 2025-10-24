package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController<User, Long> {

    private final UserService userService;

    public UserController(UserService userService) {
        super(userService, "users");
        this.userService = userService;
    }

    @Override
    protected User newInstance() {
        return new User();
    }

    // Override edit to clear password field before sending to the view
    @Override
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        user.setPassword(""); // clear password field
        model.addAttribute("item", user);
        return viewBasePath + "/form";
    }
}
