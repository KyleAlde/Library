package com.example.utility.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.utility.DatabaseConnection;
import com.example.model.Book;
import com.example.model.Book.BookStatus;

public class BookDAO {
    private final DatabaseConnection db = new DatabaseConnection();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Add new book into the database
    public void addBook(String isbn, String title, String author, String publisher, LocalDate publicationDate) throws SQLException {
        String insertQuery = "INSERT INTO books (isbn, title, author, publisher, publication_date) VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, publisher);
            ps.setDate(5, java.sql.Date.valueOf(publicationDate));
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                            READ
    //==============================================================

    //Select a book from the database
    public Book getBook(String isbn) throws SQLException {
        Book book = null;

        //Book query
        String selectBook = "SELECT * FROM books WHERE isbn = ?";

        try (PreparedStatement psBook = db.getConnection().prepareStatement(selectBook)) {
            psBook.setString(1, isbn);
            ResultSet rs = psBook.executeQuery();

            if(rs.next()) {
                book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    BookStatus.valueOf(rs.getString("status").toUpperCase())
                );
            } else {
                System.out.println("No book found with ISBN: " + isbn);
                return null;
            }
        }

        //Genre Query
        String genreQuery = """
            SELECT g.name
            FROM genres g
            JOIN book_genres bg ON g.id = bg.genre_id
            WHERE bg.book_id = ?
        """;

        try(PreparedStatement psGenre = db.getConnection().prepareStatement(genreQuery)) {
            psGenre.setString(1, isbn);
            ResultSet rs = psGenre.executeQuery();
            while(rs.next()) {
                book.addGenre(rs.getString("name"));
            }
        }

        //Available Formats Query
        String formatQuery = "SELECT format FROM available_formats WHERE isbn = ?"; 

        try(PreparedStatement psFormat = db.getConnection().prepareStatement(formatQuery)) {
            psFormat.setString(1, isbn);
            ResultSet rs = psFormat.executeQuery();
            while(rs.next()) {
                book.addAvailableFormats(rs.getString("format"));
            }
        }

        System.out.println("Query Successful");
        return book;
    }

    //Get all books from the database
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
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    BookStatus.valueOf(rs.getString("status").toUpperCase())
                );

                // Load genres for this book
                String genreQuery = """
                    SELECT g.name
                    FROM genres g
                    JOIN book_genres bg ON g.id = bg.genre_id
                    WHERE bg.book_id = ?
                """;

                try(PreparedStatement psGenre = db.getConnection().prepareStatement(genreQuery)) {
                    psGenre.setString(1, book.getIsbn());
                    ResultSet genreRs = psGenre.executeQuery();
                    while(genreRs.next()) {
                        book.addGenre(genreRs.getString("name"));
                    }
                }

                // Load available formats for this book
                String formatQuery = "SELECT format FROM available_formats WHERE isbn = ?"; 

                try(PreparedStatement psFormat = db.getConnection().prepareStatement(formatQuery)) {
                    psFormat.setString(1, book.getIsbn());
                    ResultSet formatRs = psFormat.executeQuery();
                    while(formatRs.next()) {
                        book.addAvailableFormats(formatRs.getString("format"));
                    }
                }

                books.add(book);
            }
        }

        System.out.println("Query Successful - Retrieved " + books.size() + " books");
        return books;
    }

    //==============================================================
    //                           UPDATE
    //==============================================================

    //Update status of a book in the database when borrowed or returned
    public void updateStatus(String isbn, String newStatus) throws SQLException{
        String statusQuery = """
            UPDATE books
            SET status = ?
            WHERE isbn = ?
        """;

        try (PreparedStatement ps = db.getConnection().prepareStatement(statusQuery)) {
            ps.setObject(1, newStatus, java.sql.Types.OTHER);
            ps.setString(2, isbn);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }

    //==============================================================
    //                           DELETE
    //==============================================================

    //Delete a book from the database
    public void deleteBook(String isbn) throws SQLException {
        String deleteQuery = "DELETE FROM books WHERE isbn = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, isbn);
            ps.executeUpdate();

            System.out.println("Query Successful");
        }
    }
}