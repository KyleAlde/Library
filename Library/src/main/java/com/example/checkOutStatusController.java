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
import javafx.scene.control.Separator;
import javafx.geometry.Insets;

import java.util.List;

public class checkOutStatusController {

    @FXML
    private Text accountBookAuthor;

    @FXML
    private Text accountBookStatus;

    @FXML
    private Text accountBookTitle;

    @FXML
    private HBox accountCheckoutStatus;

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
            
            // Get pending requests for current user
            List<Request> requests = requestDAO.getRequestsByBorrower(userId);
            
            if (requests.isEmpty()) {
                // Show no pending requests message
                accountBookTitle.setText("No pending checkout requests");
                accountBookAuthor.setText("");
                accountBookStatus.setText("None");
                accountBookStatus.setStyle("-fx-fill: #27ae60;"); // Green
            } else {
                // Show the most recent request (or first one)
                Request request = requests.get(0);
                Book book = bookDAO.getBook(request.getBookId());
                
                if (book != null) {
                    accountBookTitle.setText(book.getTitle());
                    accountBookAuthor.setText(book.getAuthor());
                    accountBookStatus.setText(request.getStatus().toString());
                    
                    // Set color based on status
                    setStatusColor(request.getStatus().toString());
                } else {
                    accountBookTitle.setText("Book not found");
                    accountBookAuthor.setText(request.getBookId());
                    accountBookStatus.setText(request.getStatus().toString());
                    setStatusColor(request.getStatus().toString());
                }
                
                System.out.println("DEBUG: Loaded request for book: " + request.getBookId() + 
                                 " with status: " + request.getStatus());
            }
            
        } catch (Exception e) {
            System.err.println("Error loading checkout requests: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            accountBookTitle.setText("Error loading requests");
            accountBookAuthor.setText("");
            accountBookStatus.setText("Error");
            accountBookStatus.setStyle("-fx-fill: #e74c3c;"); // Red
        }
    }
    
    private void setStatusColor(String status) {
        switch (status != null ? status.toLowerCase() : "unknown") {
            case "pending":
                accountBookStatus.setStyle("-fx-fill: #FFA500;"); // Orange
                break;
            case "approved":
                accountBookStatus.setStyle("-fx-fill: #27ae60;"); // Green
                break;
            case "rejected":
                accountBookStatus.setStyle("-fx-fill: #e74c3c;"); // Red
                break;
            default:
                accountBookStatus.setStyle("-fx-fill: #34495e;"); // Dark gray
                break;
        }
    }

    // Simple method to set book data
    public void setBookData(String title, String author, String status) {
        if (accountBookTitle != null) {
            accountBookTitle.setText(title != null ? title : "No title");
        }
        if (accountBookAuthor != null) {
            accountBookAuthor.setText(author != null ? author : "No author");
        }
        if (accountBookStatus != null) {
            accountBookStatus.setText(status != null ? status : "Unknown");
            setStatusColor(status);
        }
    }
}
