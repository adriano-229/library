// Library System - Main JavaScript

// Confirm delete action
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item?');
}

// Auto-dismiss alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});

// Form validation helper
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (form) {
        form.classList.add('was-validated');
        return form.checkValidity();
    }
    return true;
}

// Date helpers
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString();
}

// Loan date validation
function setupLoanDateValidation() {
    const loanDateInput = document.getElementById('loanDate');
    const returnDateInput = document.getElementById('returnDate');

    if (loanDateInput && returnDateInput) {
        loanDateInput.addEventListener('change', function () {
            returnDateInput.min = this.value;
            if (returnDateInput.value && returnDateInput.value < this.value) {
                returnDateInput.value = this.value;
            }
        });

        returnDateInput.addEventListener('change', function () {
            if (loanDateInput.value && this.value < loanDateInput.value) {
                alert('Return date cannot be before loan date');
                this.value = loanDateInput.value;
            }
        });
    }
}

// Book availability check
function checkBookAvailability(bookId) {
    if (!bookId) return;

    // This would typically make an AJAX call to check availability
    // For now, we'll just show a visual indicator
    const bookSelect = document.getElementById('book');
    if (bookSelect) {
        const selectedOption = bookSelect.options[bookSelect.selectedIndex];
        const available = selectedOption.getAttribute('data-available');

        const availabilityDiv = document.getElementById('book-availability');
        if (availabilityDiv) {
            if (parseInt(available) > 0) {
                availabilityDiv.innerHTML = `<span class="badge bg-success"><i class="bi bi-check-circle"></i> ${available} copies available</span>`;
            } else {
                availabilityDiv.innerHTML = `<span class="badge bg-danger"><i class="bi bi-x-circle"></i> No copies available</span>`;
            }
        }
    }
}

// Initialize tooltips
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    setupLoanDateValidation();
    initTooltips();

    // Book selection change
    const bookSelect = document.getElementById('book');
    if (bookSelect) {
        bookSelect.addEventListener('change', function () {
            checkBookAvailability(this.value);
        });
        // Check on page load if already selected
        if (bookSelect.value) {
            checkBookAvailability(bookSelect.value);
        }
    }
});

// Table search functionality
function searchTable(inputId, tableId) {
    const input = document.getElementById(inputId);
    const table = document.getElementById(tableId);

    if (!input || !table) return;

    input.addEventListener('keyup', function () {
        const filter = this.value.toUpperCase();
        const rows = table.getElementsByTagName('tr');

        for (let i = 1; i < rows.length; i++) {
            const row = rows[i];
            const cells = row.getElementsByTagName('td');
            let found = false;

            for (let j = 0; j < cells.length; j++) {
                const cell = cells[j];
                if (cell) {
                    const textValue = cell.textContent || cell.innerText;
                    if (textValue.toUpperCase().indexOf(filter) > -1) {
                        found = true;
                        break;
                    }
                }
            }

            row.style.display = found ? '' : 'none';
        }
    });
}

// Enable table search on tables with data-searchable attribute
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('tableSearch');
    const searchableTable = document.querySelector('table[data-searchable]');

    if (searchInput && searchableTable) {
        searchTable('tableSearch', searchableTable.id);
    }
});

