package com.example.utility.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.utility.DatabaseConnection;
import com.example.utility.CoverImageMatcher;
import com.example.model.Book;
import com.example.model.Book.BookStatus;

public class BookDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    // Add new book into the database
    public void addBook(String isbn, String title, String synopsis, String author, 
                       String publisher, LocalDate publicationDate) throws SQLException {
        String insertQuery = """
            INSERT INTO books (isbn, title, author, publisher, publication_date, synopsis, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setDate(5, java.sql.Date.valueOf(publicationDate));
            ps.setString(6, synopsis);
            ps.setString(7, BookStatus.AVAILABLE.toString()); // Default status
            ps.executeUpdate();

            System.out.println("Book added successfully: " + title);
        }
    }

    // Add genres for a book
    public void addGenres(String isbn, List<String> genres) throws SQLException {
        if (genres == null || genres.isEmpty()) return;
        
        String genreQuery = """
            INSERT INTO book_genres (book_id, genre_id) 
            SELECT ?, id FROM genres WHERE name = ?
            """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(genreQuery)) {
            for (String genre : genres) {
                ps.setString(1, isbn);
                ps.setString(2, genre.trim());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Genres added for book: " + isbn);
        }
    }

    // Add formats for a book
    public void addFormats(String isbn, List<String> formats) throws SQLException {
        if (formats == null || formats.isEmpty()) return;
        
        String formatQuery = "INSERT INTO available_formats (isbn, format) VALUES (?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(formatQuery)) {
            for (String format : formats) {
                ps.setString(1, isbn);
                ps.setString(2, format.trim());
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Formats added for book: " + isbn);
        }
    }

    //==============================================================
    //                            READ
    //==============================================================

    // Select a book from the database
    public Book getBook(String isbn) throws SQLException {
        Book book = null;

        // Book query
        String selectBook = "SELECT * FROM books WHERE isbn = ?";

        try (PreparedStatement psBook = db.getConnection().prepareStatement(selectBook)) {
            psBook.setString(1, isbn);
            ResultSet rs = psBook.executeQuery();

            if (rs.next()) {
                book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("synopsis"),
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    BookStatus.valueOf(rs.getString("status").toUpperCase()),
                    CoverImageMatcher.getCoverImagePath(rs.getString("title"))
                );
            } else {
                System.out.println("No book found with ISBN: " + isbn);
                return null;
            }
        }

        // Load genres
        loadGenresForBook(book);
        
        // Load available formats
        loadFormatsForBook(book);

        System.out.println("Book retrieved: " + book.getTitle());
        return book;
    }

    // Get all books from the database
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String selectAllBooks = "SELECT * FROM books ORDER BY title";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectAllBooks)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("synopsis"),
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    BookStatus.valueOf(rs.getString("status").toUpperCase()),
                    CoverImageMatcher.getCoverImagePath(rs.getString("title"))
                );

                loadGenresForBook(book);
                loadFormatsForBook(book);
                books.add(book);
            }
        }

        System.out.println("Retrieved " + books.size() + " books");
        return books;
    }

    // View cart items for a list of book IDs
    public List<Book> viewCart(List<String> bookIds) throws SQLException {
        List<Book> cartItems = new ArrayList<>();
        for (String bookId : bookIds) {
            Book book = getBook(bookId);
            if (book != null) {
                cartItems.add(book);
            }
        }
        System.out.println("Retrieved " + cartItems.size() + " cart items");
        return cartItems;
    }

    // Search for books
    public List<Book> searchBooks(String input) throws SQLException {
        List<Book> books = new ArrayList<>();
        String searchQuery = """
            SELECT DISTINCT b.* 
            FROM books b
            LEFT JOIN book_genres bg ON b.isbn = bg.book_id
            LEFT JOIN genres g ON bg.genre_id = g.id
            WHERE LOWER(b.title) LIKE LOWER(?)
               OR LOWER(b.author) LIKE LOWER(?)
               OR LOWER(b.publisher) LIKE LOWER(?)
               OR LOWER(g.name) LIKE LOWER(?)
            ORDER BY b.title
            """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(searchQuery)) {
            String likeInput = "%" + input + "%";
            ps.setString(1, likeInput);
            ps.setString(2, likeInput);
            ps.setString(3, likeInput);
            ps.setString(4, likeInput);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("synopsis"),
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    BookStatus.valueOf(rs.getString("status").toUpperCase()),
                    CoverImageMatcher.getCoverImagePath(rs.getString("title"))
                );

                loadGenresForBook(book);
                loadFormatsForBook(book);
                books.add(book);
            }
        }

        System.out.println("Search found " + books.size() + " books matching '" + input + "'");
        return books;
    }

    //==============================================================
    //                           UPDATE
    //==============================================================

    // Update book details (excluding status)
    public void updateBook(String isbn, String title, String synopsis, String author, 
                          String publisher, LocalDate publicationDate, BookStatus status) throws SQLException {
        String updateQuery = """
            UPDATE books 
            SET title = ?, author = ?, publisher = ?, publication_date = ?, 
                synopsis = ?, status = ?
            WHERE isbn = ?
            """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(updateQuery)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, publisher);
            ps.setDate(4, java.sql.Date.valueOf(publicationDate));
            ps.setString(5, synopsis);
            ps.setString(6, status.toString());
            ps.setString(7, isbn);
            
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated: " + title);
            } else {
                System.out.println("No book found with ISBN: " + isbn);
            }
        }
    }

    // Update status of a book
    public void updateStatus(String isbn, String newStatus) throws SQLException {
        String statusQuery = "UPDATE books SET status = ? WHERE isbn = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(statusQuery)) {
            ps.setObject(1, newStatus, java.sql.Types.OTHER);
            ps.setString(2, isbn);
            ps.executeUpdate();
            System.out.println("Status updated for book: " + isbn);
        }
    }

    // Update genres for a book
    public void updateGenres(String isbn, List<String> newGenres) throws SQLException {
        // First delete existing genres
        String deleteQuery = "DELETE FROM book_genres WHERE book_id = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, isbn);
            ps.executeUpdate();
        }
        
        // Then add new genres
        addGenres(isbn, newGenres);
    }

    // Update formats for a book
    public void updateFormats(String isbn, List<String> newFormats) throws SQLException {
        // First delete existing formats
        String deleteQuery = "DELETE FROM available_formats WHERE isbn = ?";
        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, isbn);
            ps.executeUpdate();
        }
        
        // Then add new formats
        addFormats(isbn, newFormats);
    }

    //==============================================================
    //                           DELETE
    //==============================================================

    // Delete a book from the database
    public void deleteBook(String isbn) throws SQLException {
        // Delete from dependent tables first (due to foreign key constraints)
        String deleteGenres = "DELETE FROM book_genres WHERE book_id = ?";
        String deleteFormats = "DELETE FROM available_formats WHERE isbn = ?";
        String deleteBook = "DELETE FROM books WHERE isbn = ?";

        try (PreparedStatement psGenres = db.getConnection().prepareStatement(deleteGenres);
             PreparedStatement psFormats = db.getConnection().prepareStatement(deleteFormats);
             PreparedStatement psBook = db.getConnection().prepareStatement(deleteBook)) {
            
            // Delete in correct order
            psGenres.setString(1, isbn);
            psGenres.executeUpdate();
            
            psFormats.setString(1, isbn);
            psFormats.executeUpdate();
            
            psBook.setString(1, isbn);
            int rowsDeleted = psBook.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Book deleted: " + isbn);
            } else {
                System.out.println("No book found with ISBN: " + isbn);
            }
        }
    }

    //==============================================================
    //                         HELPER METHODS
    //==============================================================

    private void loadGenresForBook(Book book) throws SQLException {
        String genreQuery = """
            SELECT g.name
            FROM genres g
            JOIN book_genres bg ON g.id = bg.genre_id
            WHERE bg.book_id = ?
            """;

        try (PreparedStatement psGenre = db.getConnection().prepareStatement(genreQuery)) {
            psGenre.setString(1, book.getIsbn());
            ResultSet rs = psGenre.executeQuery();
            while (rs.next()) {
                book.addGenre(rs.getString("name"));
            }
        }
    }

    // Get all available genres from database
    public List<String> getAllGenres() throws SQLException {
        List<String> genres = new ArrayList<>();
        String query = "SELECT name FROM genres ORDER BY name";
        
        try (PreparedStatement ps = db.getConnection().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                genres.add(rs.getString("name"));
            }
        }
        return genres;
    }

    private void loadFormatsForBook(Book book) throws SQLException {
        String formatQuery = "SELECT format FROM available_formats WHERE isbn = ?";

        try (PreparedStatement psFormat = db.getConnection().prepareStatement(formatQuery)) {
            psFormat.setString(1, book.getIsbn());
            ResultSet rs = psFormat.executeQuery();
            while (rs.next()) {
                book.addAvailableFormats(rs.getString("format"));
            }
        }
    }

    // Utility method to parse comma-separated strings
    public static List<String> parseCommaSeparated(String input) {
        List<String> result = new ArrayList<>();
        if (input == null || input.trim().isEmpty()) {
            return result;
        }
        
        String[] parts = input.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}