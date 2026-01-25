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

        if (validateCredentials(memberID, password)) {
            System.out.println("Login validation successful");
            // Login successful - navigate to member portal
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/memberPortal.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) returnLogin.getScene().getWindow();
                Scene scene = new Scene(root, 1200, 800);
                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error loading member portal.");
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

    private boolean validateCredentials(String memberID, String password) {
        if (memberID.isEmpty() || password.isEmpty()) {
            return false;
        }

        String query = "SELECT * FROM borrowers WHERE id = ? AND password = ?";
        
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, memberID);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a record is found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
