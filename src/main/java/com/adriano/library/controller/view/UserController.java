package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController extends BaseController<User> {

    public UserController(UserService userService) {
        super(userService, "users");
    }

    @Override
    protected User newInstance() {
        return new User();
    }
}
