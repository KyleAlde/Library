package com.example.utility.dao;

import com.example.utility.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import com.example.model.Request;
import com.example.model.Request.RequestStatus;

public class RequestDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Send borrow request
    public void sendRequest(String bookId, String borrowerId) throws SQLException {
        //Generate unique ID for borrow request
        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String id = "RQ-" + randomNumber;

        String insertQuery = "INSERT INTO borrow_requests (id, book_id, borrower_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, id);
            ps.setString(2, bookId);
            ps.setString(3, borrowerId);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                            READ
    //==============================================================

    // Get all requests for a specific borrower
    public java.util.List<Request> getRequestsByBorrower(String borrowerId) throws SQLException {
        java.util.List<Request> requests = new java.util.ArrayList<>();
        String selectQuery = "SELECT * FROM borrow_requests WHERE borrower_id = ? ORDER BY request_date DESC";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectQuery)) {
            ps.setString(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //Convert UTC into Local Time Zone
                OffsetDateTime utcRequestDate = rs.getObject("request_date", OffsetDateTime.class);
                ZonedDateTime manilaRequestDate = utcRequestDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));

                OffsetDateTime utcProcessedAt = rs.getObject("processed_at", OffsetDateTime.class);
                ZonedDateTime tempProcessedAt = null;
                OffsetDateTime manilaProcessedAt = null;

                if (utcProcessedAt != null) {
                    tempProcessedAt = utcProcessedAt.atZoneSameInstant(ZoneId.of("Asia/Manila"));
                    manilaProcessedAt = tempProcessedAt.toOffsetDateTime();
                }

                requests.add(new Request(
                    rs.getString("id"),
                    manilaRequestDate.toOffsetDateTime(),
                    manilaProcessedAt,
                    RequestStatus.valueOf(rs.getString("status").toUpperCase()),
                    rs.getString("book_id"),
                    rs.getString("borrower_id"),
                    rs.getString("processed_by")
                ));
            }

            System.out.println("Query Successful - Found " + requests.size() + " requests for borrower: " + borrowerId);
        }
        
        return requests;
    }

    //Select borrow request
    public Request selectRequest(String id) throws SQLException{
        String selectQuery = "SELECT * FROM borrow_requests WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectQuery)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                //Convert UTC into Local Time Zone
                OffsetDateTime utcRequestDate = rs.getObject("request_date", OffsetDateTime.class);
                ZonedDateTime manilaRequestDate = utcRequestDate.atZoneSameInstant(ZoneId.of("Asia/Manila"));

                OffsetDateTime utcProcessedAt = rs.getObject("processed_at", OffsetDateTime.class);
                ZonedDateTime tempProcessedAt = null;
                OffsetDateTime manilaProcessedAt = null;

                if (utcProcessedAt != null) {
                    tempProcessedAt = utcProcessedAt.atZoneSameInstant(ZoneId.of("Asia/Manila"));
                    manilaProcessedAt = tempProcessedAt.toOffsetDateTime();
                }

                return new Request(
                    rs.getString("id"),
                    manilaRequestDate.toOffsetDateTime(),
                    manilaProcessedAt,
                    RequestStatus.valueOf(rs.getString("status").toUpperCase()),
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

    //Approve borrow request
    public void approveRequest(String id, String librarianId) throws SQLException {
        String acceptQuery = """
            UPDATE borrow_requests
            SET processed_at = ?, status = ?, processed_by = ?
            WHERE id = ? 
        """;
        OffsetDateTime processedAt = OffsetDateTime.now();

        try(PreparedStatement ps =db.getConnection().prepareStatement(acceptQuery)) {
            ps.setObject(1, processedAt, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
            ps.setObject(2, "approved", java.sql.Types.OTHER);
            ps.setString(3, librarianId);
            ps.setString(4, id);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //Reject borrow request
    public void rejectRequest(String id, String librarianId) throws SQLException {
        String rejectQuery = """
            UPDATE borrow_requests
            SET processed_at = ?, status = ?, processed_by = ?
            WHERE id = ? 
        """;
        OffsetDateTime processedAt = OffsetDateTime.now();

        try(PreparedStatement ps =db.getConnection().prepareStatement(rejectQuery)) {
            ps.setObject(1, processedAt, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
            ps.setObject(2, "rejected", java.sql.Types.OTHER);
            ps.setString(3, librarianId);
            ps.setString(4, id);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                           DELETE
    //==============================================================

    //Cancel borrow request
    public void cancelRequest(String id) throws SQLException {
        String deleteQuery = "DELETE FROM borrow_requests WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, id);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }
}
