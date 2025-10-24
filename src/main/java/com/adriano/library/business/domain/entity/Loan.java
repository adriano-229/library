package com.adriano.library.business.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Loan extends BaseEntity {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate loanDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate returnDate;

    @ManyToOne(optional = false)
    private User user;

    @OneToOne(optional = false)
    private Book book;

}
