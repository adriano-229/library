package com.adriano.library.business.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Image extends BaseEntity {

    private String name;
    private String mimeType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    @OneToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;
}
