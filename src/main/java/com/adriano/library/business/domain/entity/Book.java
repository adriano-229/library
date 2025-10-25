package com.adriano.library.business.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Book extends BaseEntity {

    private String title;
    private String isbn;
    private Integer totalCopies;
    private Integer loanedCopies;
    private String imagePath;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Author> authors = new ArrayList<>();

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publisher> publishers = new ArrayList<>();

    @Transient
    public Integer getAvailableCopies() {
        return totalCopies - loanedCopies;
    }
}
