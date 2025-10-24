package com.adriano.library.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Publisher extends BaseEntity {

    private String name;

    @ManyToOne(optional = false)
    private Book book;
}
