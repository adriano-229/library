package com.adriano.library.business.persistence.repository;

import com.adriano.library.business.domain.entity.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends BaseRepository<Image, Long> {
}

