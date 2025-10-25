package com.adriano.library.controller.view;

import com.adriano.library.business.domain.entity.BaseEntity;
import com.adriano.library.business.logic.service.BaseService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

public abstract class BaseController<T extends BaseEntity> {

    protected final BaseService<T, Long> service;
    protected final String viewBasePath; // e.g., "users"

    protected BaseController(BaseService<T, Long> service, String viewBasePath) {
        this.service = service;
        this.viewBasePath = viewBasePath;
    }

    @GetMapping
    public String list(Model model) {
        List<T> all = service.findAll();
        model.addAttribute("items", all);
        return viewBasePath + "/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("item", newInstance());
        return viewBasePath + "/form";
    }

    @PostMapping
    public String save(@ModelAttribute("item") T entity) {
        if (entity.getId() == null) service.save(entity);
        else service.update(entity.getId(), entity);
        return "redirect:/" + viewBasePath;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        T entity = service.findById(id).orElseThrow();
        model.addAttribute("item", entity);
        return viewBasePath + "/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/" + viewBasePath;
    }

    protected abstract T newInstance();
}

