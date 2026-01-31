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
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
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
    private boolean isDataLoaded = false;

    @FXML
    private void initialize() {
        System.out.println("Initializing catalogue display...");
        loadCatalogue();
    }

    private void loadCatalogue() {
        try {
            System.out.println("Loading catalogue...");
            
            // Clear existing content
            catalogueContainer.getChildren().clear();
            genreSections.clear();
            
            // Get all genres that actually have books
            List<String> availableGenres = getAvailableGenres();
            System.out.println("Found genres: " + String.join(", ", availableGenres));
            
            if (availableGenres.isEmpty()) {
                showEmptyMessage("No books available in the catalogue.");
                return;
            }
            
            // Create 4 main genre sections
            createGenreSections(availableGenres);
            
            System.out.println("Catalogue loaded successfully!");
            isDataLoaded = true;
            
        } catch (Exception e) {
            System.err.println("Error loading catalogue: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Failed to load catalogue. Please try again.");
        }
    }

    private List<String> getAvailableGenres() throws SQLException {
        List<String> genres = new ArrayList<>();
        
        String query = """
            SELECT DISTINCT g.name 
            FROM genres g
            INNER JOIN book_genres bg ON g.id = bg.genre_id
            INNER JOIN books b ON bg.book_id = b.isbn
            WHERE b.status = 'available'
            ORDER BY g.name
            """;

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
        }
        
        return genres;
    }

    private void createGenreSections(List<String> availableGenres) {
        try {
            System.out.println("Creating genre sections with optimized loading...");
            
            // Define 4 main genre categories in consistent order
            Map<String, List<String>> genreCategories = new LinkedHashMap<>();
            genreCategories.put("Fiction & Literature", Arrays.asList(
                "Fantasy", "Adventure", "Mythology", "Science Fiction", "Sci-Fi", 
                "Dystopian", "Space Opera", "Mystery", "Thriller", "Crime", "Suspense",
                "Horror", "Romance", "Love Story", "Contemporary Romance",
                "Classic", "Literary Fiction", "Historical Fiction", "Young Adult", "YA", "Teen",
                "Children's Fiction", "Children?s Fiction"
            ));
            genreCategories.put("Non-Fiction & Reference", Arrays.asList(
                "Non-Fiction", "Biography", "History", "Self-Help", "Philosophy",
                "Political Science", "Sociology", "Psychology"
            ));
            genreCategories.put("Science & Technology", Arrays.asList(
                "Science", "Physics", "Astronomy", "Technology", "Engineering", "Mathematics"
            ));
            genreCategories.put("Academic & Professional", Arrays.asList(
                "Academic", "Research", "Professional", "Business", "Education"
            ));
            
            // Pre-load all books at once instead of per genre
            Map<String, List<Book>> allGenreBooks = preloadAllGenreBooks(availableGenres);
            
            // Create sections for each category in order
            for (Map.Entry<String, List<String>> entry : genreCategories.entrySet()) {
                String categoryTitle = entry.getKey();
                List<String> categoryGenres = entry.getValue();
                
                // Find which genres from this category are actually available
                List<String> availableCategoryGenres = new ArrayList<>();
                for (String genre : categoryGenres) {
                    if (availableGenres.contains(genre)) {
                        availableCategoryGenres.add(genre);
                    }
                }
                
                if (!availableCategoryGenres.isEmpty()) {
                    createOptimizedGenreSection(categoryTitle, availableCategoryGenres, allGenreBooks);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error creating genre sections: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Optimized method to preload all books for all genres at once
    private Map<String, List<Book>> preloadAllGenreBooks(List<String> genres) throws SQLException {
        System.out.println("Preloading books for " + genres.size() + " genres...");
        long startTime = System.currentTimeMillis();
        
        Map<String, List<Book>> genreBooksMap = new HashMap<>();
        
        // Create IN clause with placeholders for each genre
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("""
            SELECT DISTINCT b.*, g.name as genre_name
            FROM books b
            JOIN book_genres bg ON b.isbn = bg.book_id
            JOIN genres g ON bg.genre_id = g.id
            WHERE b.status = 'available' AND g.name IN (
            """);
        
        // Add placeholders for each genre
        for (int i = 0; i < genres.size(); i++) {
            if (i > 0) queryBuilder.append(",");
            queryBuilder.append("?");
        }
        queryBuilder.append(") ORDER BY g.name, b.title");
        
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(queryBuilder.toString())) {
            // Set parameters for each genre
            for (int i = 0; i < genres.size(); i++) {
                ps.setString(i + 1, genres.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            
            // Process results and group by genre
            while (rs.next()) {
                String genreName = rs.getString("genre_name");
                Book book = createBookFromResultSet(rs);
                
                genreBooksMap.computeIfAbsent(genreName, k -> new ArrayList<>()).add(book);
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Preloaded " + genreBooksMap.size() + " genres in " + (endTime - startTime) + "ms");
        
        return genreBooksMap;
    }
    
    // Helper method to create Book from ResultSet
    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getString("isbn"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("synopsis"),
            rs.getString("publisher"),
            rs.getDate("publication_date").toLocalDate(),
            com.example.model.Book.BookStatus.valueOf(rs.getString("status").toUpperCase()),
            com.example.utility.CoverImageMatcher.getCoverImagePath(rs.getString("title"))
        );
    }
    
    // Optimized genre section creation using preloaded books
    private void createOptimizedGenreSection(String categoryTitle, List<String> genres, Map<String, List<Book>> allGenreBooks) {
        try {
            System.out.println("Creating optimized section: " + categoryTitle);
            
            // Load section container
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            sectionContainerController controller = loader.getController();
            
            // Combine books from all genres in this category using preloaded data
            Set<String> seenIsbns = new HashSet<>();
            List<Book> categoryBooks = new ArrayList<>();
            
            for (String genre : genres) {
                List<Book> genreBooks = allGenreBooks.get(genre);
                if (genreBooks != null) {
                    System.out.println("  " + genre + ": " + genreBooks.size() + " books (preloaded)");
                    
                    // Add unique books
                    for (Book book : genreBooks) {
                        if (seenIsbns.add(book.getIsbn())) {
                            categoryBooks.add(book);
                        }
                    }
                }
            }
            
            System.out.println("  Total unique books: " + categoryBooks.size());
            
            // Set category title and display books
            controller.setGenreTitle(categoryTitle);
            controller.displayBooks(categoryBooks);
            
            // Add section to container
            catalogueContainer.getChildren().add(sectionNode);
            genreSections.put(categoryTitle, controller);
            
        } catch (Exception e) {
            System.err.println("Error creating optimized genre section for " + categoryTitle + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createGenreSection(String categoryTitle, List<String> genres) {
        try {
            System.out.println("Creating section: " + categoryTitle);
            
            // Load section container
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            sectionContainerController controller = loader.getController();
            
            // Get all books from all genres in this category
            Set<String> seenIsbns = new HashSet<>();
            List<Book> categoryBooks = new ArrayList<>();
            
            for (String genre : genres) {
                try {
                    List<Book> genreBooks = bookDAO.getBooksByGenre(genre);
                    System.out.println("  " + genre + ": " + genreBooks.size() + " books");
                    
                    // Add unique books
                    for (Book book : genreBooks) {
                        if (seenIsbns.add(book.getIsbn())) {
                            categoryBooks.add(book);
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error loading books for genre " + genre + ": " + e.getMessage());
                }
            }
            
            System.out.println("  Total unique books: " + categoryBooks.size());
            
            // Set category title and display books
            controller.setGenreTitle(categoryTitle);
            controller.displayBooks(categoryBooks);
            
            // Add section to container
            catalogueContainer.getChildren().add(sectionNode);
            genreSections.put(categoryTitle, controller);
            
        } catch (Exception e) {
            System.err.println("Error creating genre section for " + categoryTitle + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void refreshCatalogue() {
        if (isDataLoaded && !catalogueContainer.getChildren().isEmpty()) {
            System.out.println("Catalogue data already loaded, skipping refresh");
            return;
        }
        
        System.out.println("Refreshing catalogue...");
        loadCatalogue();
    }
    
    public void performSearch(String searchQuery) {
        try {
            // Clear current catalogue
            catalogueContainer.getChildren().clear();
            genreSections.clear();
            
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                showEmptyMessage("Please enter a search term");
                return;
            }
            
            // Get search results
            List<Book> searchResults = bookDAO.searchBooks(searchQuery.trim());
            
            if (searchResults.isEmpty()) {
                showEmptyMessage("No books found matching: \"" + searchQuery + "\"");
            } else {
                createSearchResultsSection(searchQuery, searchResults);
            }
            
            System.out.println("Search completed: " + searchResults.size() + " books found for \"" + searchQuery + "\"");
            
        } catch (Exception e) {
            System.err.println("Error performing search: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Search failed. Please try again.");
        }
    }
    
    private void createSearchResultsSection(String searchQuery, List<Book> books) {
        try {
            // Load section container for search results
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            sectionContainerController controller = loader.getController();
            
            // Set search title and display books
            controller.setGenreTitle("Search Results: \"" + searchQuery + "\" (" + books.size() + " books)");
            controller.displayBooks(books);
            
            // Add section to container
            catalogueContainer.getChildren().add(sectionNode);
            
            System.out.println("Created search results section with " + books.size() + " books");
            
        } catch (Exception e) {
            System.err.println("Error creating search results section: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showEmptyMessage(String message) {
        Text emptyText = new Text(message);
        emptyText.setStyle("-fx-font-size: 18px; -fx-fill: #666;");
        catalogueContainer.getChildren().add(emptyText);
    }
    
    private void showErrorMessage(String message) {
        Text errorText = new Text(message);
        errorText.setStyle("-fx-font-size: 18px; -fx-fill: #ff0000;");
        catalogueContainer.getChildren().add(errorText);
    }
}
