package com.example.utility.dao;

import com.example.model.Book;
import com.example.utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    
    private DatabaseConnection dbConnection;
    
    public CartDAO() {
        this.dbConnection = new DatabaseConnection();
    }
    
    // Add book to user's cart
    public boolean addToCart(String userId, String bookIsbn) {
        String sql = "INSERT INTO cart (book_id, borrower_id) VALUES (?, ?) " +
                     "ON CONFLICT (book_id, borrower_id) DO NOTHING";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, bookIsbn);
            pstmt.setString(2, truncatedUserId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            return false;
        }
    }
    
    // Remove book from user's cart
    public boolean removeFromCart(String userId, String bookIsbn) {
        String sql = "DELETE FROM cart WHERE borrower_id = ? AND book_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, truncatedUserId);
            pstmt.setString(2, bookIsbn);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing from cart: " + e.getMessage());
            return false;
        }
    }
    
    // Get all books in user's cart
    public List<Book> getCartItems(String userId) {
        List<Book> cartItems = new ArrayList<>();
        String sql = "SELECT b.* FROM books b " +
                     "JOIN cart c ON b.isbn = c.book_id " +
                     "WHERE c.borrower_id = ? " +
                     "ORDER BY c.book_id";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, truncatedUserId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("synopsis"),
                    rs.getString("publisher"),
                    rs.getDate("publication_date").toLocalDate(),
                    Book.BookStatus.valueOf(rs.getString("status").toUpperCase()),
                    rs.getString("cover_id")
                );
                
                cartItems.add(book);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        }
        
        return cartItems;
    }
    
    // Clear user's cart
    public boolean clearCart(String userId) {
        String sql = "DELETE FROM cart WHERE borrower_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, truncatedUserId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected >= 0; // Returns true even if no items to delete
            
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }
    
    // Check if book is in user's cart
    public boolean isBookInCart(String userId, String bookIsbn) {
        String sql = "SELECT 1 FROM cart WHERE borrower_id = ? AND book_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, truncatedUserId);
            pstmt.setString(2, bookIsbn);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking cart: " + e.getMessage());
            return false;
        }
    }
    
    // Get cart item count for user
    public int getCartItemCount(String userId) {
        String sql = "SELECT COUNT(*) FROM cart WHERE borrower_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Truncate user ID to 10 characters to match database constraint
            String truncatedUserId = userId.length() > 10 ? userId.substring(0, 10) : userId;
            
            pstmt.setString(1, truncatedUserId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting cart count: " + e.getMessage());
        }
        
        return 0;
    }
}
