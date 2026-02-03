package com.example;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.example.utility.DatabaseConnection;
import com.example.utility.UserSession;

public class loginController {

    @FXML
    private Button returnLogin;

    @FXML
    private TextField memberIDField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text verify;

    @FXML
    private void initialize() {
        System.out.println("LoginController initialized");
        returnLogin.setOnAction(event -> handleLogin());
        // Initially hide the error message completely
        verify.setManaged(false);
        verify.setVisible(false);
        System.out.println("Verify text initially hidden: managed=" + verify.isManaged() + ", visible=" + verify.isVisible());
    }

    private void handleLogin() {
        System.out.println("Login button clicked");
        String memberID = memberIDField.getText().trim();
        String password = passwordField.getText().trim();
        System.out.println("Credentials entered - ID: '" + memberID + "', Password: '" + password + "'");

        UserData userData = validateCredentials(memberID, password);
        if (userData != null) {
            System.out.println("Login validation successful - User type: " + userData.userType);
            
            // Create user session
            UserSession session = UserSession.getInstance();
            session.createSession(userData.userId, userData.userName, userData.userType);
            
            // Login successful - navigate to appropriate portal
            try {
                String fxmlPath = "librarian".equals(userData.userType) ? "fxml/LibrarianPage/librarianPortal.fxml" : "fxml/memberPortal.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                
                Stage stage = (Stage) returnLogin.getScene().getWindow();
                Scene scene = stage.getScene();
                
                // Keep the current window state, just replace the scene content
                scene.setRoot(root);
                
                // Maximize the window if not already maximized
                if (!stage.isMaximized()) {
                    stage.setMaximized(true);
                }
                
                System.out.println("Switched to " + userData.userType + " portal");
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading portal.");
            }
        } else {
            System.out.println("Login validation failed");
            // Login failed - show error message
            showError("Incorrect ID or Password, please try again.");
        }
    }

    private void showError(String message) {
        verify.setText(message);
        verify.setManaged(true); // Allow to take space
        verify.setVisible(true);
    }

    private UserData validateCredentials(String memberID, String password) {
        if (memberID.isEmpty() || password.isEmpty()) {
            return null;
        }

        // First check if it's a librarian
        String librarianQuery = "SELECT * FROM librarians WHERE id = ? AND password = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(librarianQuery)) {
            
            pstmt.setString(1, memberID);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                return new UserData(rs.getString("id"), fullName, "librarian");
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking librarian credentials: " + e.getMessage());
        }

        // Then check if it's a borrower
        String borrowerQuery = "SELECT * FROM borrowers WHERE id = ? AND password = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(borrowerQuery)) {
            
            pstmt.setString(1, memberID);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                return new UserData(rs.getString("id"), fullName, "borrower");
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking borrower credentials: " + e.getMessage());
        }
        
        return null; // No match found
    }

    // Inner class to hold user data
    private static class UserData {
        String userId;
        String userName;
        String userType;

        UserData(String userId, String userName, String userType) {
            this.userId = userId;
            this.userName = userName;
            this.userType = userType;
        }
    }

}
