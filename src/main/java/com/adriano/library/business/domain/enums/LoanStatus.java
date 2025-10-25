package com.adriano.library.business.domain.enums;

public enum LoanStatus {
    RESERVED,  // Loan date is in the future
    ACTIVE,    // Loan is currently active (between loan date and return date)
    RETURNED   // Return date has passed
}

