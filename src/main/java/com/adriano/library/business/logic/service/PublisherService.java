package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Publisher;
import com.adriano.library.business.persistence.repository.PublisherRepository;
import org.springframework.stereotype.Service;

@Service
public class PublisherService extends BaseService<Publisher, Long> {

    public PublisherService(PublisherRepository repository) {
        super(repository);
    }
}

