package com.example.model;

import java.time.OffsetDateTime;

public class Request {
    private String id;
    private OffsetDateTime requestDate;
    private OffsetDateTime processedAt;
    private RequestStatus status;
    private String bookId;
    private String borrowerId;
    private String processedBy;

    public Request(String id, OffsetDateTime requestDate, OffsetDateTime processedAt, RequestStatus status, String bookId, String borrowerId, String processedBy) {
        this.id = id;
        this.requestDate = requestDate;
        this.processedAt = processedAt;
        this.status= status;
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.processedBy = processedBy;
    }

    public String getId() { return id; }
    public OffsetDateTime getRequestDate() { return requestDate; }
    public OffsetDateTime getProcessedAt() { return processedAt; }
    public RequestStatus getStatus() { return status; }
    public String getBookId() { return bookId; }
    public String getBorrowerId() { return borrowerId; }
    public String getLibrarianId() { return processedBy; }

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
