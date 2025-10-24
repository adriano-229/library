package com.adriano.library.business.persistence.repository;

import com.adriano.library.business.domain.entity.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends BaseRepository<Book, Long> {
}

