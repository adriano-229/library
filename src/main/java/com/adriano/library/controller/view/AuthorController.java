package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Author;
import com.adriano.library.business.logic.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorController extends BaseController<Author, Long> {

    public AuthorController(AuthorService service) {
        super(service, "authors");
    }

    @Override
    protected Author newInstance() {
        return new Author();
    }
}

