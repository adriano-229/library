package com.adriano.library.business.persistence.repository;

import com.adriano.library.business.domain.entity.Publisher;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends BaseRepository<Publisher, Long> {
}

