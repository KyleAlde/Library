package com.example.utility.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Book;
import com.example.utility.DatabaseConnection;

public class CartDAO {
    private final DatabaseConnection db = new DatabaseConnection();
    private final BookDAO bookDAO = new BookDAO();

    //==============================================================
    //                           CREATE
    //==============================================================

    //Add item to cart
    public void addToCart(String bookId, String borrowerId) throws SQLException {
        String insertQuery = "INSERT INTO cart (book_id, borrower_id) VALUES (?, ?)";

        try (PreparedStatement ps = db.getConnection().prepareStatement(insertQuery)) {
            ps.setString(1, bookId);
            ps.setString(2, borrowerId);
            ps.executeUpdate();

            System.out.println("Item added to cart successfully");
        }
    }
    
    //==============================================================
    //                            READ
    //==============================================================

    //Get cart item IDs for a borrower
    public List<String> getCartItems(String borrowerId) throws SQLException {
        List<String> cartItemIds = new ArrayList<>();
        String selectCartBookIds = "SELECT book_id FROM cart WHERE borrower_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(selectCartBookIds)) {
            ps.setString(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String bookId = rs.getString("book_id");
                cartItemIds.add(bookId);
            }
        }

        System.out.println("Query Successful - Retrieved " + cartItemIds.size() + " cart item IDs for borrower " + borrowerId);
        return cartItemIds;
    }

    //==============================================================
    //                           DELETE
    //==============================================================

    //Remove item from cart
    public void removeItem(String bookId, String borrowerId) throws SQLException {
        String deleteQuery = "DELETE FROM cart WHERE book_id = ? AND borrower_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, bookId);
            ps.setString(2, borrowerId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Item removed from cart successfully");
            } else {
                System.out.println("No matching item found in cart to remove");
            }
        }
    }

    //Remove all items from a borrower's cart
    public void removeAllItems(String borrowerId) throws SQLException {
        String deleteQuery = "DELETE FROM cart WHERE borrower_id = ?";

        try (PreparedStatement ps = db.getConnection().prepareStatement(deleteQuery)) {
            ps.setString(1, borrowerId);
            int rowsAffected = ps.executeUpdate();

            System.out.println(rowsAffected + " items removed from cart for borrower " + borrowerId);
        }
    }
}
