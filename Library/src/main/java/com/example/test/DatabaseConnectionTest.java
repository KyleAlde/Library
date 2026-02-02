package com.example.test;

import com.example.utility.dao.CartDAO;
import com.example.utility.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===");
        
        // Test 1: Basic Database Connection
        testDatabaseConnection();
        
        // Test 2: CartDAO Initialization
        testCartDAO();
        
        System.out.println("=== Test Complete ===");
    }
    
    private static void testDatabaseConnection() {
        System.out.println("\n1. Testing Database Connection...");
        
        try {
            DatabaseConnection dbConnection = new DatabaseConnection();
            Connection conn = dbConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection SUCCESSFUL");
                System.out.println("   Connection valid: " + conn.isValid(5));
                System.out.println("   Database URL: " + conn.getMetaData().getURL());
            } else {
                System.out.println("❌ Database connection FAILED - Connection is null or closed");
            }
            
            conn.close();
            
        } catch (SQLException e) {
            System.out.println("❌ Database connection FAILED");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCartDAO() {
        System.out.println("\n2. Testing CartDAO...");
        
        try {
            CartDAO cartDAO = new CartDAO();
            System.out.println("✅ CartDAO initialization SUCCESSFUL");
            
            // Test cart item count (this will test the connection)
            String testUserId = "test_user_123";
            int itemCount = cartDAO.getCartItemCount(testUserId);
            System.out.println("✅ CartDAO query SUCCESSFUL");
            System.out.println("   Test user cart item count: " + itemCount);
            
        } catch (Exception e) {
            System.out.println("❌ CartDAO test FAILED");
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
