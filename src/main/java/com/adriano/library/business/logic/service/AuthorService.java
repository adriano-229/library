package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Author;
import com.adriano.library.business.persistence.repository.AuthorRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends BaseService<Author, Long> {

    public AuthorService(AuthorRepository repository) {
        super(repository);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void save(Author entity) {
        super.save(entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void update(Long id, Author entity) {
        super.update(id, entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}

