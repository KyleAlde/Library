package com.example;

import com.example.utility.dao.LoanDAO;
import com.example.utility.dao.BookDAO;
import com.example.utility.UserSession;
import com.example.model.Loan;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;

import java.util.List;

public class checkoutHistoryController {

    @FXML
    private VBox historyContainer;

    private LoanDAO loanDAO;
    private BookDAO bookDAO;

    public void initialize() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
        
        // Load current user's checkout history
        loadCheckoutHistory();
    }

    // Load checkout history for current user
    public void loadCheckoutHistory() {
        try {
            String borrowerId = UserSession.getInstance().getUserId();
            System.out.println("DEBUG: Loading checkout history for borrower: " + borrowerId);
            
            // Clear existing content
            historyContainer.getChildren().clear();
            
            // Add header
            Label headerLabel = new Label("My Checkout History");
            headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            historyContainer.getChildren().add(headerLabel);
            historyContainer.getChildren().add(new Separator());
            
            // Get all loans (including returned ones) - use existing method and filter
            List<Loan> activeLoans = loanDAO.getLoansByBorrower(borrowerId);
            System.out.println("DEBUG: Found " + activeLoans.size() + " active loans");
            
            if (activeLoans.isEmpty()) {
                // Show no history message
                Label noHistoryLabel = new Label("No checkout history found");
                noHistoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
                historyContainer.getChildren().add(noHistoryLabel);
            } else {
                // Display all loans
                for (Loan loan : activeLoans) {
                    displayLoan(loan);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading checkout history: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message
            historyContainer.getChildren().clear();
            Label errorLabel = new Label("Error loading checkout history. Please try again.");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            historyContainer.getChildren().add(errorLabel);
        }
    }

    private void displayLoan(Loan loan) {
        try {
            HBox loanBox = new HBox(10);
            loanBox.setPadding(new Insets(5));
            
            // Determine status and style
            boolean isReturned = loan.getReturnDate() != null;
            String statusColor = isReturned ? "#ecf0f1" : "#d5f4e6"; // Light gray for returned, light green for active
            String borderColor = isReturned ? "#bdc3c7" : "#27ae60"; // Gray for returned, green for active
            
            loanBox.setStyle("-fx-background-color: " + statusColor + "; -fx-border-color: " + borderColor + "; -fx-border-radius: 5;");
            
            // Get book details
            Book book = bookDAO.getBook(loan.getBookId());
            String bookTitle = book != null ? book.getTitle() : "Unknown Book";
            
            // Loan info
            VBox loanInfo = new VBox(2);
            Label titleLabel = new Label(bookTitle);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            Label loanDateLabel = new Label("Borrowed: " + loan.getLoanDate().toString());
            loanDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            
            Label dueDateLabel = new Label("Due: " + loan.getDueDate().toString());
            dueDateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            
            Label statusLabel;
            if (isReturned) {
                statusLabel = new Label("Returned: " + loan.getReturnDate().toString());
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
            } else {
                statusLabel = new Label("Status: Active");
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
            
            loanInfo.getChildren().addAll(titleLabel, loanDateLabel, dueDateLabel, statusLabel);
            loanBox.getChildren().add(loanInfo);
            
            historyContainer.getChildren().add(loanBox);
            
        } catch (Exception e) {
            System.err.println("Error displaying loan: " + e.getMessage());
        }
    }

    // Method to refresh the history display
    public void refreshHistory() {
        loadCheckoutHistory();
    }
}
