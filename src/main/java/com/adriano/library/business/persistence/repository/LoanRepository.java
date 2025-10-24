package com.adriano.library.business.persistence.repository;

import com.adriano.library.business.domain.entity.Loan;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends BaseRepository<Loan, Long> {
}

