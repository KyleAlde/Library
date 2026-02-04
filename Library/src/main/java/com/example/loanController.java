package com.example;

import com.example.utility.dao.LoanDAO;
import com.example.utility.dao.BookDAO;
import com.example.model.Loan;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.util.List;

public class loanController {

    @FXML
    private Text loanItemNo;

    @FXML
    private VBox loansItemContainer;

    @FXML
    private Button loansReturn;

    @FXML
    private Button loansReturnAll;

    private final LoanDAO loanDAO = new LoanDAO();
    private final BookDAO bookDAO = new BookDAO();
    private List<Loan> currentLoans;
    private List<loanItemController> loanItemControllers = new java.util.ArrayList<>();

    @FXML
    private void initialize() {
        loadUserLoans();
        setupReturnButton();
    }

    private void loadUserLoans() {
        try {
            // Get current user from session
            String borrowerId = com.example.utility.UserSession.getInstance().getUserId();
            System.out.println("DEBUG: Loading loans for user: " + borrowerId);
            
            // Load user's loans
            currentLoans = loanDAO.getLoansByBorrower(borrowerId);
            System.out.println("DEBUG: Retrieved " + currentLoans.size() + " loans from database");
            
            // Clear existing items
            loansItemContainer.getChildren().clear();
            loanItemControllers.clear();
            
            // Create loan item for each loan
            for (Loan loan : currentLoans) {
                try {
                    System.out.println("DEBUG: Processing loan - ID: " + loan.getId() + ", Book: " + loan.getBookId());
                    
                    // Get book details
                    Book book = bookDAO.getBook(loan.getBookId());
                    if (book != null) {
                        System.out.println("DEBUG: Found book - Title: " + book.getTitle());
                        
                        // Load loan item FXML
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/loanItem.fxml"));
                        Node loanItemNode = loader.load();
                        
                        // Get controller and set data
                        loanItemController itemController = loader.getController();
                        itemController.setLoanData(loan, book);
                        itemController.setParentController(this);
                        
                        // Add to container and track controller
                        loansItemContainer.getChildren().add(loanItemNode);
                        loanItemControllers.add(itemController);
                        
                        System.out.println("DEBUG: Added loan item to UI for: " + book.getTitle());
                    } else {
                        System.out.println("DEBUG: Book not found for loan ID: " + loan.getId());
                    }
                } catch (Exception e) {
                    System.err.println("Error loading loan item: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            updateItemCount();
            System.out.println("Loaded " + currentLoans.size() + " loans for user: " + borrowerId);
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to load loans: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void setupReturnButton() {
        loansReturn.setOnAction(event -> handleReturnBooks());
        loansReturnAll.setOnAction(event -> handleReturnAllBooks());
    }

    @FXML
    private void handleReturnBooks() {
        System.out.println("DEBUG: Return button clicked");
        
        // Get selected loan items
        List<Loan> selectedLoans = getSelectedLoans();
        System.out.println("DEBUG: Found " + selectedLoans.size() + " selected loans for return");
        
        if (selectedLoans.isEmpty()) {
            System.out.println("DEBUG: No loans selected, showing warning");
            showAlert("Warning", "Please select books to return", AlertType.WARNING);
            return;
        }

        try {
            System.out.println("DEBUG: Processing returns for " + selectedLoans.size() + " loans");
            
            // Process returns
            for (Loan loan : selectedLoans) {
                System.out.println("DEBUG: Returning loan - ID: " + loan.getId() + ", Book: " + loan.getBookId());
                boolean success = loanDAO.returnBook(loan.getId());
                System.out.println("DEBUG: Loan return " + (success ? "successful" : "failed") + " for ID: " + loan.getId());
            }
            
            showAlert("Success", "Returned " + selectedLoans.size() + " book(s) successfully!", AlertType.INFORMATION);
            
            // Reload loans
            System.out.println("DEBUG: Reloading loans after return");
            loadUserLoans();
            
        } catch (SQLException e) {
            System.err.println("DEBUG: Error during return process: " + e.getMessage());
            showAlert("Error", "Failed to return books: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    private void handleReturnAllBooks() {
        System.out.println("DEBUG: Return All button clicked");
        
        if (currentLoans == null || currentLoans.isEmpty()) {
            System.out.println("DEBUG: No loans available to return");
            showAlert("Information", "No books to return", AlertType.INFORMATION);
            return;
        }

        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Return All");
        confirmAlert.setHeaderText("Return All Books");
        confirmAlert.setContentText("Are you sure you want to return all " + currentLoans.size() + " book(s)?");

        if (confirmAlert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK) {
            try {
                System.out.println("DEBUG: Processing return for all " + currentLoans.size() + " loans");
                
                // Auto-select all items for visual feedback
                for (loanItemController itemController : loanItemControllers) {
                    itemController.setSelected(true);
                }
                updateItemCount(); // Update the counter to show all items selected
                
                int successCount = 0;
                int failCount = 0;
                
                // Process all loans
                for (Loan loan : currentLoans) {
                    System.out.println("DEBUG: Returning loan - ID: " + loan.getId() + ", Book: " + loan.getBookId());
                    boolean success = loanDAO.returnBook(loan.getId());
                    
                    if (success) {
                        successCount++;
                        System.out.println("DEBUG: Loan return successful for ID: " + loan.getId());
                    } else {
                        failCount++;
                        System.err.println("DEBUG: Loan return failed for ID: " + loan.getId());
                    }
                }
                
                if (failCount == 0) {
                    showAlert("Success", "All " + successCount + " book(s) returned successfully!", AlertType.INFORMATION);
                } else {
                    showAlert("Partial Success", successCount + " book(s) returned successfully. " + failCount + " book(s) failed to return.", AlertType.WARNING);
                }
                
                // Reload loans
                System.out.println("DEBUG: Reloading loans after return all");
                loadUserLoans();
                
            } catch (SQLException e) {
                System.err.println("DEBUG: Error during return all process: " + e.getMessage());
                showAlert("Error", "Failed to return books: " + e.getMessage(), AlertType.ERROR);
            }
        } else {
            System.out.println("DEBUG: User cancelled return all operation");
        }
    }

    private List<Loan> getSelectedLoans() {
        List<Loan> selectedLoans = new java.util.ArrayList<>();
        System.out.println("DEBUG: Checking " + loanItemControllers.size() + " loan item controllers for selection");
        
        for (loanItemController itemController : loanItemControllers) {
            if (itemController.isSelected()) {
                selectedLoans.add(itemController.getLoan());
                System.out.println("DEBUG: Selected loan - ID: " + itemController.getLoan().getId());
            }
        }
        
        System.out.println("DEBUG: Total selected loans: " + selectedLoans.size());
        return selectedLoans;
    }

    public void updateItemCount() {
        int selectedCount = getSelectedLoans().size();
        loanItemNo.setText(String.valueOf(selectedCount));
        System.out.println("DEBUG: Updated item count to: " + selectedCount);
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to refresh loans (can be called from other controllers)
    public void refreshLoans() {
        loadUserLoans();
    }
}
