package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.persistence.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService extends BaseService<Loan, Long> {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository repository, BookService bookService) {
        super(repository);
        this.loanRepository = repository;
        this.bookService = bookService;
    }

    @Override
    public void beforeSave(Loan loan) {
        validateLoan(loan);
        updateBookCopies(loan.getBook(), 1); // Increment loaned copies
    }

    @Override
    public void beforeUpdate(Long id, Loan loan) {
        validateLoan(loan);

        // Get the old loan to check if book changed
        Loan oldLoan = loanRepository.findById(id).orElse(null);
        if (oldLoan != null && !oldLoan.getBook().getId().equals(loan.getBook().getId())) {
            // Book changed - return old book and loan new book
            updateBookCopies(oldLoan.getBook(), -1);
            updateBookCopies(loan.getBook(), 1);
        }
    }

    @Override
    public void beforeDelete(Long id) {
        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan != null) {
            updateBookCopies(loan.getBook(), -1); // Decrement loaned copies
        }
    }

    private void validateLoan(Loan loan) {
        // Validate dates
        if (loan.getLoanDate() == null || loan.getReturnDate() == null) {
            throw new IllegalArgumentException("Loan date and return date are required");
        }

        // Return date must be at least loan date + 1 day
        if (!loan.getReturnDate().isAfter(loan.getLoanDate())) {
            throw new IllegalArgumentException("Return date must be after loan date");
        }

        // Validate book availability
        Book book = loan.getBook();
        if (book == null) {
            throw new IllegalArgumentException("Book is required");
        }

        // Count active and reserved loans for this book
        int occupiedCopies = countOccupiedCopies(book.getId(), loan.getId());

        if (occupiedCopies >= book.getTotalCopies()) {
            throw new IllegalArgumentException("No available copies for this book at the selected dates");
        }
    }

    private int countOccupiedCopies(Long bookId, Long excludeLoanId) {
        List<Loan> loans = loanRepository.findAll();
        return (int) loans.stream()
                .filter(l -> l.getBook().getId().equals(bookId))
                .filter(l -> !l.getId().equals(excludeLoanId))
                .filter(l -> l.getStatus() != com.adriano.library.business.domain.enums.LoanStatus.RETURNED)
                .count();
    }

    private void updateBookCopies(Book book, int delta) {
        int currentLoaned = book.getLoanedCopies() != null ? book.getLoanedCopies() : 0;
        book.setLoanedCopies(currentLoaned + delta);
        bookService.update(book.getId(), book);
    }

    public List<Loan> findActiveLoans() {
        return loanRepository.findAll().stream()
                .filter(Loan::isActive)
                .toList();
    }

    public List<Loan> findReservedLoans() {
        return loanRepository.findAll().stream()
                .filter(Loan::isReserved)
                .toList();
    }
}

