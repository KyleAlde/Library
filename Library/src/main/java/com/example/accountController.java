package com.example;

import com.example.utility.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class accountController {

    @FXML
    private VBox checkHistory;

    @FXML
    private VBox checkStatus;

    @FXML
    private Text memberFullName;

    @FXML
    private Text memberType;

    @FXML
    private Button returnLogout;

    public void initialize() {
        // Load user session data
        loadUserData();
        
        // Load checkout data from database
        loadCheckoutData();
    }

    private void loadUserData() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            memberFullName.setText(session.getUserName());
            memberType.setText(session.getUserType());
        }
    }

    private void loadCheckoutData() {
        UserSession session = UserSession.getInstance();
        if (session.isLoggedIn()) {
            String userId = session.getUserId();
            
            // Load checkout status
            loadCheckoutStatus(userId);
            
            // Load checkout history
            loadCheckoutHistory(userId);
        }
    }

    private void loadCheckoutStatus(String userId) {
        try {
            // Create a status controller to load the data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/checkoutStatus.fxml"));
            HBox statusContainer = loader.load();
            // The checkout status will load automatically in initialize()
            
            // Add to the status container
            checkStatus.getChildren().add(statusContainer);
            
        } catch (Exception e) {
            System.err.println("Error loading checkout status: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message instead of fallback
            Label errorLabel = new Label("Error loading checkout status");
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
            checkStatus.getChildren().add(errorLabel);
        }
    }

    private void loadCheckoutHistory(String userId) {
        try {
            // Create a history controller to load the data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/checkoutHistory.fxml"));
            VBox historyContainer = loader.load();
            // The checkout history will load automatically in initialize()
            
            // Add to the history container
            checkHistory.getChildren().add(historyContainer);
            
        } catch (Exception e) {
            System.err.println("Error loading checkout history: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message instead of fallback
            Label errorLabel = new Label("Error loading checkout history");
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
            checkHistory.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        try {
            App.setRoot("fxml/welcomePage/welcomePortal");
        } catch (Exception e) {
            System.err.println("Error logging out: " + e.getMessage());
        }
    }
}
