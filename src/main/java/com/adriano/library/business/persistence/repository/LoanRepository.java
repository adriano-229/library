package com.adriano.library.business.persistence.repository;

import com.adriano.library.business.domain.entity.Loan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
}
