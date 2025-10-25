package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.logic.service.BookService;
import com.adriano.library.business.logic.service.LoanService;
import com.adriano.library.business.logic.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String create(Model model, Authentication authentication) {
        Loan loan = newInstance();

        // If not admin, pre-set the current user
        if (isAdmin(authentication)) {
            User currentUser = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("Current user not found"));
            loan.setUser(currentUser);
            model.addAttribute("currentUserId", currentUser.getId());
        }

        model.addAttribute("item", loan);
        addBooksAndUsers(model);
        return viewBasePath + "/form";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Loan entity = loanService.findById(id).orElseThrow();
        model.addAttribute("item", entity);
        addBooksAndUsers(model);
        return viewBasePath + "/form";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String save(@ModelAttribute("item") Loan entity, RedirectAttributes redirectAttributes, Model model, Authentication authentication) {
        try {
            // If not admin, ensure user is creating loan for themselves
            if (isAdmin(authentication)) {
                User currentUser = userService.findByEmail(authentication.getName())
                        .orElseThrow(() -> new IllegalStateException("Current user not found"));
                entity.setUser(currentUser);
            }

            if (entity.getId() == null) {
                loanService.save(entity);
            } else {
                loanService.update(entity.getId(), entity);
            }
            redirectAttributes.addFlashAttribute("success", "Loan saved successfully");
            return "redirect:/" + viewBasePath;
        } catch (IllegalArgumentException | SecurityException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("item", entity);
            addBooksAndUsers(model);
            return viewBasePath + "/form";
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String list(Model model) {
        List<Loan> all = loanService.findAll();
        model.addAttribute("items", all);
        return viewBasePath + "/list";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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

    private boolean isAdmin(Authentication authentication) {
        return authentication == null || authentication.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

