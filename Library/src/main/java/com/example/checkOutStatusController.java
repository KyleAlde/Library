package com.example;

import com.example.utility.dao.RequestDAO;
import com.example.utility.dao.BookDAO;
import com.example.utility.UserSession;
import com.example.model.Request;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Label;

import java.util.List;

public class checkOutStatusController {

    @FXML
    private VBox accountCheckoutStatus;

    @FXML
    private Text sectionHeader;

    @FXML
    private VBox requestsContainer;

    private RequestDAO requestDAO;
    private BookDAO bookDAO;

    public void initialize() {
        requestDAO = new RequestDAO();
        bookDAO = new BookDAO();
        
        // Method to refresh the status display
        refreshStatus();
    }
    
    public void refreshStatus() {
        loadCheckoutRequests();
    }
    
    private void loadCheckoutRequests() {
        try {
            String userId = UserSession.getInstance().getUserId();
            System.out.println("DEBUG: Loading checkout requests for user: " + userId);
            
            // Clear existing requests
            requestsContainer.getChildren().clear();
            
            // Get pending requests for current user
            List<Request> requests = requestDAO.getRequestsByBorrower(userId);
            
            if (requests.isEmpty()) {
                // Show no pending requests message
                Label noRequestsLabel = new Label("No pending checkout requests");
                noRequestsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60;");
                requestsContainer.getChildren().add(noRequestsLabel);
            } else {
                // Show all requests
                for (Request request : requests) {
                    displayRequest(request);
                }
                
                System.out.println("DEBUG: Loaded " + requests.size() + " requests for user");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading checkout requests: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            Label errorLabel = new Label("Error loading requests");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            requestsContainer.getChildren().add(errorLabel);
        }
    }
    
    private void displayRequest(Request request) {
        try {
            Book book = bookDAO.getBook(request.getBookId());
            
            // Create HBox for this request
            HBox requestRow = new HBox(10);
            requestRow.setStyle("-fx-padding: 5px; -fx-background-color: #f8f9fa; -fx-border-radius: 5px;");
            
            // Title
            Text titleText = new Text(book != null ? book.getTitle() : "Unknown Book");
            titleText.setWrappingWidth(250);
            titleText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            // Author
            Text authorText = new Text(book != null ? book.getAuthor() : "Unknown Author");
            authorText.setWrappingWidth(200);
            authorText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            
            // Status
            Text statusText = new Text(request.getStatus().toString());
            statusText.setWrappingWidth(150);
            statusText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            setStatusColor(statusText, request.getStatus().toString());
            
            // Add to row
            requestRow.getChildren().addAll(titleText, authorText, statusText);
            
            // Add to container
            requestsContainer.getChildren().add(requestRow);
            
            System.out.println("DEBUG: Displayed request for book: " + request.getBookId() + 
                             " with status: " + request.getStatus());
            
        } catch (Exception e) {
            System.err.println("Error displaying request: " + e.getMessage());
            
            // Show error row for this request
            HBox errorRow = new HBox(10);
            errorRow.setStyle("-fx-padding: 5px; -fx-background-color: #ffe6e6; -fx-border-radius: 5px;");
            
            Text errorText = new Text("Error loading book: " + request.getBookId());
            errorText.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");
            
            errorRow.getChildren().add(errorText);
            requestsContainer.getChildren().add(errorRow);
        }
    }
    
    private void setStatusColor(Text statusText, String status) {
        switch (status != null ? status.toLowerCase() : "unknown") {
            case "pending":
                statusText.setStyle("-fx-fill: #FFA500; -fx-font-size: 12px; -fx-font-weight: bold;");
                break;
            case "approved":
                statusText.setStyle("-fx-fill: #27ae60; -fx-font-size: 12px; -fx-font-weight: bold;");
                break;
            case "rejected":
                statusText.setStyle("-fx-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
                break;
            default:
                statusText.setStyle("-fx-fill: #34495e; -fx-font-size: 12px; -fx-font-weight: bold;");
                break;
        }
    }
}
