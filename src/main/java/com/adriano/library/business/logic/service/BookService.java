package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.persistence.repository.BookRepository;
import org.springframework.stereotype.Service;

@Service
public class BookService extends BaseService<Book, Long> {

    public BookService(BookRepository repository) {
        super(repository);
    }
}

