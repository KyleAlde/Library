package com.example;

import com.example.utility.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
            checkOutStatusController controller = loader.getController();
            
            // Load actual checkout status from database
            controller.loadCheckoutStatus(userId);
            
            // Add to the status container
            checkStatus.getChildren().add(statusContainer);
            
        } catch (Exception e) {
            System.err.println("Error loading checkout status: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple item
            createSimpleStatusItem();
        }
    }

    private void loadCheckoutHistory(String userId) {
        try {
            // Create a history controller to load the data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/checkoutHistory.fxml"));
            HBox historyContainer = loader.load();
            checkoutHistoryController controller = loader.getController();
            
            // Load actual checkout history from database
            controller.loadCheckoutHistory(userId);
            
            // Add to the history container
            checkHistory.getChildren().add(historyContainer);
            
        } catch (Exception e) {
            System.err.println("Error loading checkout history: " + e.getMessage());
            e.printStackTrace();
            // Fallback to simple item
            createSimpleHistoryItem();
        }
    }

    private void createSimpleStatusItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/checkoutStatus.fxml"));
            HBox statusItem = loader.load();
            checkOutStatusController controller = loader.getController();
            
            // Set simple data
            controller.setBookData("Sample Request", "Sample Author", "pending");
            
            checkStatus.getChildren().add(statusItem);
            
        } catch (Exception e) {
            System.err.println("Error creating status item: " + e.getMessage());
        }
    }

    private void createSimpleHistoryItem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/checkoutHistory.fxml"));
            HBox historyItem = loader.load();
            checkoutHistoryController controller = loader.getController();
            
            // Set simple data
            controller.setBookData("Sample Book Title", "Sample Author", "Jan 15, 2024");
            
            checkHistory.getChildren().add(historyItem);
            
        } catch (Exception e) {
            System.err.println("Error creating history item: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        try {
            App.setRoot("login");
        } catch (Exception e) {
            System.err.println("Error logging out: " + e.getMessage());
        }
    }
}
