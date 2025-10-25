package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.domain.entity.Publisher;
import com.adriano.library.business.logic.service.BookService;
import com.adriano.library.business.logic.service.PublisherService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/publishers")
@PreAuthorize("hasRole('ADMIN')")
public class PublisherController extends BaseController<Publisher> {

    private final BookService bookService;

    public PublisherController(PublisherService service, BookService bookService) {
        super(service, "publishers");
        this.bookService = bookService;
    }

    @Override
    protected Publisher newInstance() {
        return new Publisher();
    }

    @Override
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("item", newInstance());
        addBooks(model);
        return viewBasePath + "/form";
    }

    @Override
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Publisher entity = service.findById(id).orElseThrow();
        model.addAttribute("item", entity);
        addBooks(model);
        return viewBasePath + "/form";
    }

    private void addBooks(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
    }
}
