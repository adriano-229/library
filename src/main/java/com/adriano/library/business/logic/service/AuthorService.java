package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Author;
import com.adriano.library.business.persistence.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends BaseService<Author, Long> {

    public AuthorService(AuthorRepository repository) {
        super(repository);
    }
}

