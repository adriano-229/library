package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.persistence.repository.LoanRepository;
import org.springframework.stereotype.Service;

@Service
public class LoanService extends BaseService<Loan, Long> {

    public LoanService(LoanRepository repository) {
        super(repository);
    }
}

