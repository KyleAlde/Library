package com.example.utility.dao;

import com.example.utility.DatabaseConnection;
import com.example.model.Request;
import com.example.model.Request.RequestStatus;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorrowRequestDAO {
    
    private DatabaseConnection dbConnection;
    
    public BorrowRequestDAO() {
        this.dbConnection = new DatabaseConnection();
    }
    
    // Create a borrow request for a book
    public boolean createBorrowRequest(String bookIsbn, String borrowerId) {
        String sql = "INSERT INTO borrow_requests (id, book_id, borrower_id, status) VALUES (?, ?, ?, 'pending')";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Generate unique ID for the request (max 7 characters for database constraint)
            String requestId = "R" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            
            // Truncate borrower ID to 10 characters to match database constraint
            String truncatedBorrowerId = borrowerId.length() > 10 ? borrowerId.substring(0, 10) : borrowerId;
            
            System.out.println("DEBUG: Request ID: '" + requestId + "' (length: " + requestId.length() + ")");
            System.out.println("DEBUG: Original borrower ID: '" + borrowerId + "' (length: " + borrowerId.length() + ")");
            System.out.println("DEBUG: Truncated borrower ID: '" + truncatedBorrowerId + "' (length: " + truncatedBorrowerId.length() + ")");
            
            pstmt.setString(1, requestId);
            pstmt.setString(2, bookIsbn);
            pstmt.setString(3, truncatedBorrowerId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating borrow request: " + e.getMessage());
            return false;
        }
    }
    
    // Create borrow requests for multiple books (for checkout)
    public boolean createBorrowRequests(java.util.List<String> bookIsbns, String borrowerId) {
        boolean allSuccess = true;
        
        for (String bookIsbn : bookIsbns) {
            if (!createBorrowRequest(bookIsbn, borrowerId)) {
                allSuccess = false;
                System.err.println("Failed to create borrow request for book: " + bookIsbn);
            }
        }
        
        return allSuccess;
    }
    
    // Get all pending requests for a borrower
    public java.util.List<String> getPendingRequests(String borrowerId) {
        java.util.List<String> requestIds = new java.util.ArrayList<>();
        String sql = "SELECT id FROM borrow_requests WHERE borrower_id = ? AND status = 'pending'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate borrower ID to 10 characters to match database constraint
            String truncatedBorrowerId = borrowerId.length() > 10 ? borrowerId.substring(0, 10) : borrowerId;
            
            pstmt.setString(1, truncatedBorrowerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                requestIds.add(rs.getString("id"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pending requests: " + e.getMessage());
        }
        
        return requestIds;
    }
    
    // Get book ID from request ID
    public String getBookIdFromRequest(String requestId) {
        String sql = "SELECT book_id FROM borrow_requests WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("book_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting book ID from request: " + e.getMessage());
        }
        
        return null;
    }
    
    // Get all pending requests with book details for a borrower
    public java.util.List<RequestDetails> getPendingRequestsWithDetails(String borrowerId) {
        java.util.List<RequestDetails> requests = new java.util.ArrayList<>();
        String sql = "SELECT id, book_id FROM borrow_requests WHERE borrower_id = ? AND status = 'pending'";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate borrower ID to 10 characters to match database constraint
            String truncatedBorrowerId = borrowerId.length() > 10 ? borrowerId.substring(0, 10) : borrowerId;
            
            pstmt.setString(1, truncatedBorrowerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String requestId = rs.getString("id");
                String bookId = rs.getString("book_id");
                requests.add(new RequestDetails(requestId, bookId, "pending"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting pending requests with details: " + e.getMessage());
        }
        
        return requests;
    }
    
    // Inner class to hold request details
    public static class RequestDetails {
        private String requestId;
        private String bookId;
        private String status;
        
        public RequestDetails(String requestId, String bookId, String status) {
            this.requestId = requestId;
            this.bookId = bookId;
            this.status = status;
        }
        
        public String getRequestId() { return requestId; }
        public String getBookId() { return bookId; }
        public String getStatus() { return status; }
    }
    
    // Get all requests for librarian management
    public List<Request> getAllRequests() throws SQLException {
        List<Request> requests = new ArrayList<>();
        String sql = "SELECT id, request_date, processed_at, status, book_id, borrower_id, processed_by FROM borrow_requests ORDER BY request_date DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Request request = new Request(
                    rs.getString("id"),
                    rs.getTimestamp("request_date") != null ? 
                        rs.getTimestamp("request_date").toLocalDateTime().atOffset(OffsetDateTime.now().getOffset()) : null,
                    rs.getTimestamp("processed_at") != null ? 
                        rs.getTimestamp("processed_at").toLocalDateTime().atOffset(OffsetDateTime.now().getOffset()) : null,
                    RequestStatus.valueOf(rs.getString("status").toUpperCase()),
                    rs.getString("book_id"),
                    rs.getString("borrower_id"),
                    rs.getString("processed_by")
                );
                requests.add(request);
            }
        }
        
        return requests;
    }
    
    // Update request status
    public boolean updateRequestStatus(String requestId, RequestStatus newStatus) throws SQLException {
        String sql = "UPDATE borrow_requests SET status = ?, processed_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newStatus.toString().toLowerCase());
            pstmt.setString(2, requestId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
