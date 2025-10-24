package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.logic.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BookController extends BaseController<Book, Long> {

    public BookController(BookService service) {
        super(service, "books");
    }

    @Override
    protected Book newInstance() {
        return new Book();
    }
}

