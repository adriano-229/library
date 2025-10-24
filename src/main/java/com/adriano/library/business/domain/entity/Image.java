package com.adriano.library.business.domain.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Image extends Book {

    private String name;
    private String mimeType;
    private byte[] content;
}
