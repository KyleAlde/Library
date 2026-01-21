package com.example.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

public class Loan {
    private String id;
    private OffsetDateTime loanDate;
    private LocalDate dueDate;
    private OffsetDateTime returnDate;
    private BigDecimal fineAmount;
    private String bookId;
    private String borrowerID;
    private String processedBy;

    public Loan(String id, OffsetDateTime loanDate, LocalDate dueDate, OffsetDateTime returnDate, BigDecimal fineAmount, String bookId, String borrowerID, String processedBy) {
        this.id = id;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.bookId = bookId;
        this.borrowerID = borrowerID;
        this.processedBy = processedBy;
    }

    public String getId() { return id; }
    public OffsetDateTime getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public OffsetDateTime getReturnDate() { return returnDate; }
    public BigDecimal getFineAmount() { return fineAmount; }
    public String getBookId() { return bookId; }
    public String getBorrowerId() { return borrowerID; }
    public String getLibrarianId() { return processedBy; }
}