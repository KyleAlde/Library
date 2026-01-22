package com.example.utility.dao;

import com.example.utility.DatabaseConnection;
import com.example.model.Borrower;
import com.example.model.Borrower.BorrowerType;

import java.sql.*;
import java.util.Random;

public class BorrowerDAO {
    DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Create new borrower account
    public void createBorrower(String lastName, String firstName, int age, String type, String email, String password) {
        //Generate unique ID for borrower
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        String borrowerID = "2024-" + randomNumber;

        String insertQuery = "INSERT INTO borrowers (id, last_name, first_name, age, type, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, borrowerID);
            ps.setString(2, lastName);
            ps.setString(3, firstName);
            ps.setInt(4, age);
            ps.setObject(5, type.toLowerCase(), java.sql.Types.OTHER);
            ps.setString(6, email);
            ps.setString(7, password);
            ps.executeUpdate();

            System.out.println("Borrower account created successfully with ID: " + borrowerID);
        } catch (SQLException e) {
            System.err.println("Error creating borrower account: " + e.getMessage());
        }
    }

    //==============================================================
    //                            READ
    //==============================================================
    
    //Select a borrower from the database
    public Borrower getBorrower(String borrowerID) throws SQLException {
        String selectQuery = "SELECT * FROM borrowers WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectQuery)) {
            ps.setString(1, borrowerID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                Borrower borrower = new Borrower(
                    rs.getString("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getInt("age"),
                    BorrowerType.valueOf(rs.getString("type").toUpperCase()),
                    rs.getString("email"),
                    rs.getString("password")
                );
                return borrower;
            }

            System.out.println("Query Successful");
        }            
        return null;
    }

    //==============================================================
    //                           UPDATE
    //==============================================================

    //Update borrower information
    public void updateBorrower(String borrowerId, String columnName, String newValue) throws SQLException {
        String updateQuery = "UPDATE borrowers SET " + columnName + " = ? WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(updateQuery)) {
            if (columnName.equals("type")) {
                ps.setObject(1, newValue, java.sql.Types.OTHER);
            } else if (columnName.equals("age")) {
                ps.setInt(1, Integer.parseInt(newValue));
            } else {
                ps.setString(1, newValue);
            }
            ps.setString(2, borrowerId);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                           DELETE
    //==============================================================

    //Delete a borrower account from the database
    public void deleteBorrower(String borrowerID) throws SQLException {
        String deleteQuery = "DELETE FROM borrowers WHERE id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, borrowerID);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }
}
