package com.example.utility.dao;

import com.example.utility.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.example.model.Librarian;

public class LibrarianDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                            READ
    //==============================================================

    //Select librarian via librarian ID when creating loan
    public Librarian selectLibrarian(String id) throws Exception {
        String selectLibrarian = "SELECT * FROM librarians WHERE id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(selectLibrarian)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                Librarian librarian  = new Librarian(
                    rs.getString("id"),
                    rs.getString("last_name"),
                    rs.getString("first_name"),
                    rs.getString("email"),
                    rs.getString("password")
                );

                System.out.println("Query Successful");
                return librarian;
            } else {
                throw new Exception("Librarian not found");
            }
        }
    }
}
