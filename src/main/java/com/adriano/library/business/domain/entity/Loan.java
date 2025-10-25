package com.adriano.library.business.domain.entity;

import com.adriano.library.business.domain.enums.LoanStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
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

    @Transient
    public LoanStatus getStatus() {
        LocalDate today = LocalDate.now();

        if (loanDate.isAfter(today)) {
            return LoanStatus.RESERVED;
        } else if (returnDate.isBefore(today)) {
            return LoanStatus.RETURNED;
        } else {
            return LoanStatus.ACTIVE;
        }
    }

    @Transient
    public boolean isActive() {
        return getStatus() == LoanStatus.ACTIVE;
    }

    @Transient
    public boolean isReserved() {
        return getStatus() == LoanStatus.RESERVED;
    }
}
