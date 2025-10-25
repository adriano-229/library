package com.adriano.library.business.logic.service;

import com.adriano.library.business.domain.entity.Book;
import com.adriano.library.business.domain.entity.Loan;
import com.adriano.library.business.domain.entity.User;
import com.adriano.library.business.persistence.repository.BookRepository;
import com.adriano.library.business.persistence.repository.LoanRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanService extends BaseService<Loan, Long> {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public LoanService(LoanRepository repository, BookRepository bookRepository, UserService userService) {
        super(repository);
        this.loanRepository = repository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public void save(Loan loan) {
        // If not admin, ensure user is creating loan for themselves
        if (!isAdmin()) {
            String currentUserEmail = getCurrentUserEmail();
            User currentUser = userService.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new IllegalStateException("Current user not found"));

            if (loan.getUser() == null || !loan.getUser().getId().equals(currentUser.getId())) {
                throw new SecurityException("You can only create loans for yourself");
            }
        }
        super.save(loan);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void update(Long id, Loan loan) {
        super.update(id, loan);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        super.deleteById(id);
    }

    // Visibility restriction: ADMIN -> all, USER -> own only
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Loan> findAll() {
        if (isAdmin()) {
            return super.findAll();
        }
        String currentUserEmail = getCurrentUserEmail();
        User currentUser = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
        return loanRepository.findByUserId(currentUser.getId());
    }

    // Defensive: prevent non-admins from fetching someone else's loan by id
    @Override
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Optional<Loan> findById(Long id) {
        Optional<Loan> opt = super.findById(id);
        if (isAdmin() || opt.isEmpty()) return opt;
        String currentUserEmail = getCurrentUserEmail();
        User currentUser = userService.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
        Loan loan = opt.get();
        if (!loan.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not allowed to access this loan");
        }
        return opt;
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
        loanRepository.findById(id).ifPresent(loan ->
                updateBookCopies(loan.getBook(), -1) // Decrement loaned copies
        );
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
        bookRepository.findById(book.getId()).ifPresent(b -> {
            int currentLoaned = b.getLoanedCopies() != null ? b.getLoanedCopies() : 0;
            b.setLoanedCopies(currentLoaned + delta);
            bookRepository.save(b);
        });
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
