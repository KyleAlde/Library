package com.example.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.example.utility.dao.BookDAO;
import com.example.model.Book;

public class DebugGenres {
    
    public static void main(String[] args) {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            String dbUrl = "jdbc:postgresql://ep-long-tree-ahfewdq1-pooler.c-3.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_xUDkPfQ6IVB2&sslmode=require&channelBinding=require";
            
            System.out.println("=== GENRE DEBUG ANALYSIS ===");
            
            // 1. Check all genres in database
            System.out.println("\n1. ALL GENRES IN DATABASE:");
            System.out.println("=========================");
            
            String genreQuery = "SELECT name FROM genres ORDER BY name";
            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement pstmt = conn.prepareStatement(genreQuery);
                 ResultSet rs = pstmt.executeQuery()) {
                
                List<String> allGenres = new ArrayList<>();
                while (rs.next()) {
                    String genre = rs.getString("name");
                    allGenres.add(genre);
                    System.out.println("- " + genre);
                }
                
                // 2. Check books per genre
                System.out.println("\n2. BOOKS PER GENRE:");
                System.out.println("==================");
                
                String booksPerGenreQuery = """
                    SELECT g.name, COUNT(bg.book_id) as book_count
                    FROM genres g
                    LEFT JOIN book_genres bg ON g.id = bg.genre_id
                    GROUP BY g.name
                    ORDER BY book_count DESC
                    """;
                
                try (PreparedStatement pstmt2 = conn.prepareStatement(booksPerGenreQuery);
                     ResultSet rs2 = pstmt2.executeQuery()) {
                    
                    while (rs2.next()) {
                        String genre = rs2.getString("name");
                        int count = rs2.getInt("book_count");
                        System.out.println(genre + ": " + count + " books");
                    }
                }
                
                // 3. Check Science Fiction specifically
                System.out.println("\n3. SCIENCE FICTION GENRE ANALYSIS:");
                System.out.println("==================================");
                
                String sciFiQuery = """
                    SELECT b.title, b.author
                    FROM books b
                    JOIN book_genres bg ON b.isbn = bg.book_id
                    JOIN genres g ON bg.genre_id = g.id
                    WHERE g.name = 'Science Fiction'
                    ORDER BY b.title
                    """;
                
                try (PreparedStatement pstmt3 = conn.prepareStatement(sciFiQuery);
                     ResultSet rs3 = pstmt3.executeQuery()) {
                    
                    System.out.println("Books directly in 'Science Fiction' genre:");
                    while (rs3.next()) {
                        System.out.println("- " + rs3.getString("title") + " by " + rs3.getString("author"));
                    }
                }
                
                // 4. Check related genres
                System.out.println("\n4. RELATED GENRES ANALYSIS:");
                System.out.println("==========================");
                
                List<String> sciFiRelated = List.of("Science Fiction", "Sci-Fi", "Dystopian", "Space Opera");
                
                for (String relatedGenre : sciFiRelated) {
                    String relatedQuery = """
                        SELECT COUNT(bg.book_id) as count
                        FROM book_genres bg
                        JOIN genres g ON bg.genre_id = g.id
                        WHERE g.name = ?
                        """;
                    
                    try (PreparedStatement pstmt4 = conn.prepareStatement(relatedQuery)) {
                        pstmt4.setString(1, relatedGenre);
                        ResultSet rs4 = pstmt4.executeQuery();
                        
                        if (rs4.next()) {
                            int count = rs4.getInt("count");
                            System.out.println(relatedGenre + ": " + count + " books");
                        }
                    }
                }
                
                // 5. Simulate our grouping logic
                System.out.println("\n5. SIMULATED GROUPING LOGIC:");
                System.out.println("===========================");
                
                BookDAO bookDAO = new BookDAO();
                Map<String, List<String>> genreMappings = new HashMap<>();
                genreMappings.put("Science Fiction", List.of("Science Fiction", "Sci-Fi", "Dystopian", "Space Opera"));
                
                for (Map.Entry<String, List<String>> entry : genreMappings.entrySet()) {
                    String groupName = entry.getKey();
                    List<String> relatedGenres = entry.getValue();
                    
                    List<String> availableGenres = new ArrayList<>();
                    int totalBookCount = 0;
                    
                    for (String genre : relatedGenres) {
                        if (allGenres.contains(genre)) {
                            availableGenres.add(genre);
                            try {
                                List<Book> genreBooks = bookDAO.getBooksByGenre(genre);
                                totalBookCount += genreBooks.size();
                                System.out.println("  " + genre + ": " + genreBooks.size() + " books");
                            } catch (SQLException e) {
                                System.err.println("Error counting books for genre " + genre + ": " + e.getMessage());
                            }
                        }
                    }
                    
                    System.out.println("TOTAL " + groupName + " group: " + totalBookCount + " books");
                    System.out.println("Available genres: " + String.join(", ", availableGenres));
                }
                
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
