package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import com.example.utility.dao.BookDAO;
import com.example.utility.DatabaseConnection;
import com.example.model.Book;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class displayCatalogueController {

    @FXML
    private AnchorPane displayCatalogueLHolder;

    @FXML
    private VBox catalogueContainer;

    private final BookDAO bookDAO = new BookDAO();
    private final DatabaseConnection dbConnection = new DatabaseConnection();
    private Map<String, sectionContainerController> genreSections = new HashMap<>();

    @FXML
    private void initialize() {
        try {
            loadCatalogue();
            checkUnusedGenres(); // Check which genres aren't being used
        } catch (Exception e) {
            System.err.println("Error initializing catalogue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCatalogue() {
        try {
            // Get all genres that actually have books
            List<String> genresWithBooks = getGenresWithBooks();
            
            if (genresWithBooks.isEmpty()) {
                System.out.println("No genres with books found");
                return;
            }
            
            // Load books for each genre using sectionContainer layout
            for (String genre : genresWithBooks) {
                loadGenreSection(genre);
            }
            
            System.out.println("Catalogue loaded with " + genresWithBooks.size() + " genres containing books");
            
        } catch (Exception e) {
            System.err.println("Error loading catalogue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<String> getGenresWithBooks() throws SQLException {
        List<String> genresWithBooks = new java.util.ArrayList<>();
        
        String query = """
            SELECT g.name, COUNT(b.isbn) as book_count
            FROM genres g
            INNER JOIN book_genres bg ON g.id = bg.genre_id
            INNER JOIN books b ON bg.book_id = b.isbn
            WHERE b.status = 'available'
            GROUP BY g.name
            HAVING COUNT(b.isbn) > 0
            ORDER BY book_count DESC, g.name ASC
            """;

        try (java.sql.PreparedStatement ps = dbConnection.getConnection().prepareStatement(query);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String genreName = rs.getString("name");
                int bookCount = rs.getInt("book_count");
                genresWithBooks.add(genreName);
                System.out.println("Genre: " + genreName + " - " + bookCount + " books");
            }
        }
        
        return genresWithBooks;
    }

    private void loadGenreSection(String genre) {
        try {
            // Load sectionContainer for this genre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            sectionContainerController controller = loader.getController();
            genreSections.put(genre, controller);
            
            // Load books for this genre
            List<Book> books = bookDAO.getBooksByGenre(genre);
            
            // Set the genre title and load books into the section
            controller.setGenreTitle(genre);
            controller.displayBooks(books);
            
            // Add section to main catalogue container
            catalogueContainer.getChildren().add(sectionNode);
            
            System.out.println("Loaded " + books.size() + " books for genre: " + genre);
            
        } catch (Exception e) {
            System.err.println("Error loading genre section for " + genre + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshCatalogue() {
        catalogueContainer.getChildren().clear();
        genreSections.clear();
        loadCatalogue();
    }
    
    public void performSearch(String searchQuery) {
        try {
            // Clear current catalogue
            catalogueContainer.getChildren().clear();
            genreSections.clear();
            
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                // Show empty search message
                Text noSearchText = new Text("Please enter a search term");
                noSearchText.setStyle("-fx-font-size: 18px; -fx-fill: #666;");
                catalogueContainer.getChildren().add(noSearchText);
                return;
            }
            
            // Get search results
            List<Book> searchResults = bookDAO.searchBooks(searchQuery.trim());
            
            if (searchResults.isEmpty()) {
                // Show no results message
                Text noResultsText = new Text("No books found matching the criteria");
                noResultsText.setStyle("-fx-font-size: 18px; -fx-fill: #666;");
                catalogueContainer.getChildren().add(noResultsText);
            } else {
                // Create a single section for all search results
                createSearchResultsSection(searchQuery, searchResults);
            }
            
            System.out.println("Search completed: " + searchResults.size() + " books found for \"" + searchQuery + "\"");
            
        } catch (Exception e) {
            System.err.println("Error performing search: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            Text errorText = new Text("Error occurred while searching. Please try again.");
            errorText.setStyle("-fx-font-size: 18px; -fx-fill: #ff0000;");
            catalogueContainer.getChildren().add(errorText);
        }
    }
    
    private void createSearchResultsSection(String searchQuery, List<Book> books) {
        try {
            // Load sectionContainer for search results
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            sectionContainerController controller = loader.getController();
            
            // Set the search title and load books into the section
            controller.setGenreTitle("Books with matching results to \"" + searchQuery + "\"");
            controller.displayBooks(books);
            
            // Add section to catalogue container
            catalogueContainer.getChildren().add(sectionNode);
            
            System.out.println("Created search results section with " + books.size() + " books");
            
        } catch (Exception e) {
            System.err.println("Error creating search results section: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkUnusedGenres() {
        try {
            String query = """
                SELECT g.name 
                FROM genres g
                LEFT JOIN book_genres bg ON g.id = bg.genre_id
                LEFT JOIN books b ON bg.book_id = b.isbn
                WHERE bg.genre_id IS NULL OR b.isbn IS NULL
                ORDER BY g.name
                """;
            
            System.out.println("\n=== GENRES WITH NO BOOKS ===");
            
            try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(query);
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
            
            // Also show total vs used
            String totalQuery = "SELECT COUNT(*) as total FROM genres";
            String usedQuery = """
                SELECT COUNT(DISTINCT g.id) as used 
                FROM genres g
                INNER JOIN book_genres bg ON g.id = bg.genre_id
                INNER JOIN books b ON bg.book_id = b.isbn
                WHERE b.status = 'available'
                """;
            
            try (PreparedStatement ps1 = dbConnection.getConnection().prepareStatement(totalQuery);
                 PreparedStatement ps2 = dbConnection.getConnection().prepareStatement(usedQuery);
                 ResultSet rs1 = ps1.executeQuery();
                 ResultSet rs2 = ps2.executeQuery()) {
                
                if (rs1.next() && rs2.next()) {
                    int total = rs1.getInt("total");
                    int used = rs2.getInt("used");
                    int unused = total - used;
                    
                    System.out.println("\n=== GENRE SUMMARY ===");
                    System.out.println("Total genres: " + total);
                    System.out.println("Used genres: " + used);
                    System.out.println("Unused genres: " + unused);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking unused genres: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
