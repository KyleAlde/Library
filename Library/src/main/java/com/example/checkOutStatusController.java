package com.example;

import com.example.utility.dao.RequestDAO;
import com.example.utility.dao.BookDAO;
import com.example.model.Request;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

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
            
            // Set color based on status
            switch (status != null ? status.toLowerCase() : "unknown") {
                case "pending":
                    accountBookStatus.setStyle("-fx-fill: #FFA500;"); // Orange
                    break;
                case "approved":
                    accountBookStatus.setStyle("-fx-fill: #00FF00;"); // Green
                    break;
                case "rejected":
                    accountBookStatus.setStyle("-fx-fill: #FF0000;"); // Red
                    break;
                default:
                    accountBookStatus.setStyle("-fx-fill: #808080;"); // Gray
                    break;
            }
        }
    }

    // Load checkout status using existing DAO
    public void loadCheckoutStatus(String borrowerId) {
        try {
            System.out.println("DEBUG: Loading checkout status for borrower: " + borrowerId);
            
            // Get all requests for the borrower using existing DAO
            List<Request> requests = requestDAO.getRequestsByBorrower(borrowerId);
            System.out.println("DEBUG: Found " + requests.size() + " requests");
            
            if (requests.isEmpty()) {
                // Show no requests message
                setBookData("No pending requests", "", "All requests processed");
                accountBookStatus.setStyle("-fx-fill: #00FF00;"); // Green
            } else {
                // Display each request
                for (Request request : requests) {
                    displayRequest(request);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading checkout status: " + e.getMessage());
            e.printStackTrace();
            setBookData("Error loading status", "Please try again", "Error");
            accountBookStatus.setStyle("-fx-fill: #FF0000;"); // Red
        }
    }

    private void displayRequest(Request request) {
        try {
            // Get book details using existing DAO
            Book book = bookDAO.getBook(request.getBookId());
            
            if (book != null) {
                setBookData(book.getTitle(), book.getAuthor(), request.getStatus().toString());
            } else {
                setBookData("Unknown Book", "Unknown Author", request.getStatus().toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error displaying request: " + e.getMessage());
            setBookData("Error", "", request.getStatus().toString());
        }
    }

    // Getter for the HBox container
    public HBox getAccountCheckoutStatus() {
        return accountCheckoutStatus;
    }
}
