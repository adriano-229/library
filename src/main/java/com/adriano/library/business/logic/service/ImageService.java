package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Image;
import com.adriano.library.business.persistence.repository.ImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageService extends BaseService<Image, Long> {

    public ImageService(ImageRepository repository) {
        super(repository);
    }
}

