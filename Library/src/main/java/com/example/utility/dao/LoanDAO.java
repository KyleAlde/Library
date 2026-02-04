package com.example.utility.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

import com.example.utility.DatabaseConnection;
import com.example.model.Loan;

public class LoanDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Create loan upon confirmation from librarian
    public void createLoan(String bookId, String borrowerId, String processedBy) throws SQLException{
        //Generate unique ID for borrow request
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String id = "L-" + randomNumber;

        LocalDate dueDate = LocalDate.now().plusDays(14);
        String insertQuery = "INSERT INTO loans (id, due_date, book_id, borrower_id, processed_by) VALUES (?, ?, ? , ?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, id);
            ps.setDate(2, java.sql.Date.valueOf(dueDate));
            ps.setString(3, bookId);
            ps.setString(4, borrowerId);
            ps.setString(5, processedBy);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                            READ
    //==============================================================

    // Get all loans for a specific borrower
    public java.util.List<Loan> getLoansByBorrower(String borrowerId) throws SQLException {
        java.util.List<Loan> loans = new java.util.ArrayList<>();
        String selectQuery = "SELECT * FROM loans WHERE borrower_id = ? AND return_date IS NULL ORDER BY loan_date DESC";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectQuery)) {
            ps.setString(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //Convert UTC into Local Time Zone
                OffsetDateTime utcLoanDate = rs.getObject("loan_date", OffsetDateTime.class);
                ZonedDateTime manilaLoanDate = utcLoanDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));

                OffsetDateTime utcReturnDate = rs.getObject("return_date", OffsetDateTime.class);
                ZonedDateTime tempManilaReturnDate = null;
                OffsetDateTime manilaReturnDate = null;

                if (utcReturnDate != null) {
                    tempManilaReturnDate = utcReturnDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));
                    manilaReturnDate = tempManilaReturnDate.toOffsetDateTime();
                }
                
                loans.add(new Loan(
                    rs.getString("id"),
                    manilaLoanDate.toOffsetDateTime(),
                    rs.getDate("due_date").toLocalDate(),
                    manilaReturnDate,
                    rs.getBigDecimal("fine_amount"),
                    rs.getString("book_id"),
                    rs.getString("borrower_id"),
                    rs.getString("processed_by")
                ));
            }

            System.out.println("Query Successful - Found " + loans.size() + " loans for borrower: " + borrowerId);
        }
        
        return loans;
    }

    //Select a loan from the database
    public Loan selectLoan(String id) throws SQLException {
        String selectLoan = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(selectLoan)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                //Convert UTC into Local Time Zone
                OffsetDateTime utcLoanDate = rs.getObject("loan_date", OffsetDateTime.class);
                ZonedDateTime manilaLoanDate = utcLoanDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));

                OffsetDateTime utcReturnDate = rs.getObject("return_date", OffsetDateTime.class);
                ZonedDateTime tempManilaReturnDate = null;
                OffsetDateTime manilaReturnDate = null;

                if (utcReturnDate != null) {
                    tempManilaReturnDate = utcReturnDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));
                    manilaReturnDate = tempManilaReturnDate.toOffsetDateTime();
                }
                
                return new Loan(
                    rs.getString("id"),
                    manilaLoanDate.toOffsetDateTime(),
                    rs.getDate("due_date").toLocalDate(),
                    manilaReturnDate,
                    rs.getBigDecimal("fine_amount"),
                    rs.getString("book_id"),
                    rs.getString("borrower_id"),
                    rs.getString("processed_by")
                );
            }
            System.out.println("Query Successful");
        }
        return null;
    }

    //==============================================================
    //                           UPDATE
    //==============================================================

    //Update return date when book is returned
    public void updateReturnDate(String id) throws SQLException {
        String returnQuery = """
            UPDATE loans
            SET return_date = ?
            WHERE id = ?
        """;
        OffsetDateTime returnDate = OffsetDateTime.now(ZoneOffset.UTC);

        try (PreparedStatement ps = db.getConnection().prepareStatement(returnQuery)) {
            ps.setObject(1, returnDate, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
            ps.setString(2, id);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }
    
    // Return a book (update return date)
    public boolean returnBook(String loanId) throws SQLException {
        String sql = "UPDATE loans SET return_date = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement ps = db.getConnection().prepareStatement(sql)) {
            ps.setString(1, loanId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
