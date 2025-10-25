package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.BookService;
import com.adriano.library.business.logic.service.LoanService;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/loans")
public class LoanController {

    private final BookService bookService;
    private final UserService userService;
    private final LoanService loanService;
    private final String viewBasePath = "loans";

    public LoanController(LoanService service, BookService bookService, UserService userService) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanService = service;
    }

    protected Loan newInstance() {
        return new Loan();
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("item", newInstance());
        addBooksAndUsers(model);
        return viewBasePath + "/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Loan entity = loanService.findById(id).orElseThrow();
        model.addAttribute("item", entity);
        addBooksAndUsers(model);
        return viewBasePath + "/form";
    }

    @PostMapping
    public String save(@ModelAttribute("item") Loan entity, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (entity.getId() == null) {
                loanService.save(entity);
            } else {
                loanService.update(entity.getId(), entity);
            }
            redirectAttributes.addFlashAttribute("success", "Loan saved successfully");
            return "redirect:/" + viewBasePath;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("item", entity);
            addBooksAndUsers(model);
            return viewBasePath + "/form";
        }
    }

    @GetMapping
    public String list(Model model) {
        List<Loan> all = loanService.findAll();
        model.addAttribute("items", all);
        return viewBasePath + "/list";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        loanService.deleteById(id);
        return "redirect:/" + viewBasePath;
    }

    private void addBooksAndUsers(Model model) {
        List<Book> books = bookService.findAll();
        List<User> users = userService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("users", users);
    }
}

