package com.example;

import java.sql.*;

public class CheckUnusedGenres {
    private static final String DATABASE_URL = "jdbc:postgresql://ep-long-tree-ahfewdq1-pooler.c-3.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_xUDkPfQ6IVB2&sslmode=require&channelBinding=require";
    
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            
            // Query to find all genres with no books
            String query = """
                SELECT g.name 
                FROM genres g
                LEFT JOIN book_genres bg ON g.id = bg.genre_id
                LEFT JOIN books b ON bg.book_id = b.isbn
                WHERE bg.genre_id IS NULL OR b.isbn IS NULL
                ORDER BY g.name
                """;
            
            System.out.println("Genres with no books:");
            System.out.println("====================");
            
            try (PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                
                boolean foundUnused = false;
                while (rs.next()) {
                    String genreName = rs.getString("name");
                    System.out.println("- " + genreName);
                    foundUnused = true;
                }
                
                if (!foundUnused) {
                    System.out.println("All genres have books associated with them!");
                }
            }
            
            // Also show total genres and used genres
            String totalQuery = "SELECT COUNT(*) as total FROM genres";
            String usedQuery = """
                SELECT COUNT(DISTINCT g.id) as used 
                FROM genres g
                INNER JOIN book_genres bg ON g.id = bg.genre_id
                """;
            
            try (PreparedStatement ps1 = conn.prepareStatement(totalQuery);
                 PreparedStatement ps2 = conn.prepareStatement(usedQuery);
                 ResultSet rs1 = ps1.executeQuery();
                 ResultSet rs2 = ps2.executeQuery()) {
                
                if (rs1.next() && rs2.next()) {
                    int total = rs1.getInt("total");
                    int used = rs2.getInt("used");
                    int unused = total - used;
                    
                    System.out.println("\nSummary:");
                    System.out.println("Total genres: " + total);
                    System.out.println("Used genres: " + used);
                    System.out.println("Unused genres: " + unused);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
