package com.example.utility.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.example.utility.DatabaseConnection;
import com.example.model.Loan;

public class LoanDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Create loan upon confirmation from librarian
    public void createLoan(String id, String bookId, String borrowerID, String processedBy) throws SQLException{
        LocalDate dueDate = LocalDate.now().plusDays(14);
        String insertQuery = "INSERT INTO loans (id, due_date, book_id, borrower_id, processed_by) VALUES (?, ?, ? , ?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, id);
            ps.setDate(2, java.sql.Date.valueOf(dueDate));
            ps.setString(3, bookId);
            ps.setString(4, borrowerID);
            ps.setString(5, processedBy);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                            READ
    //==============================================================

    //Select a loan from the database
    public Loan selectLoan(String id) throws SQLException {
        String selectLoan = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(selectLoan)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                OffsetDateTime utcLoanDate = rs.getObject("loan_date", OffsetDateTime.class);
                ZonedDateTime manilaLoanDate = utcLoanDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));

                OffsetDateTime utcReturnDate = rs.getObject("return_date", OffsetDateTime.class);
                ZonedDateTime TempManilaReturnDate = null;
                OffsetDateTime manilaReturnDate = null;
                if (utcReturnDate != null) {
                    TempManilaReturnDate = utcReturnDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));
                    manilaReturnDate = TempManilaReturnDate.toOffsetDateTime();
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
}
