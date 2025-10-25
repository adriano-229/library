package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Publisher;
import com.adriano.library.business.persistence.repository.PublisherRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PublisherService extends BaseService<Publisher, Long> {

    public PublisherService(PublisherRepository repository) {
        super(repository);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void save(Publisher entity) {
        super.save(entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void update(Long id, Publisher entity) {
        super.update(id, entity);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        super.deleteById(id);
    }
}

