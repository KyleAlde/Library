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
            
            // Load user's loans
            currentLoans = loanDAO.getLoansByBorrower(borrowerId);
            
            // Clear existing items
            loansItemContainer.getChildren().clear();
            loanItemControllers.clear();
            
            // Create loan item for each loan
            for (Loan loan : currentLoans) {
                try {
                    // Get book details
                    Book book = bookDAO.getBook(loan.getBookId());
                    if (book != null) {
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
                    }
                } catch (Exception e) {
                    System.err.println("Error loading loan item: " + e.getMessage());
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
    }

    @FXML
    private void handleReturnBooks() {
        // Get selected loan items
        List<Loan> selectedLoans = getSelectedLoans();
        
        if (selectedLoans.isEmpty()) {
            showAlert("Warning", "Please select books to return", AlertType.WARNING);
            return;
        }

        try {
            // Process returns
            for (Loan loan : selectedLoans) {
                loanDAO.returnBook(loan.getId());
            }
            
            showAlert("Success", "Returned " + selectedLoans.size() + " book(s) successfully!", AlertType.INFORMATION);
            
            // Reload loans
            loadUserLoans();
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to return books: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private List<Loan> getSelectedLoans() {
        List<Loan> selectedLoans = new java.util.ArrayList<>();
        for (loanItemController itemController : loanItemControllers) {
            if (itemController.isSelected()) {
                selectedLoans.add(itemController.getLoan());
            }
        }
        return selectedLoans;
    }

    public void updateItemCount() {
        int selectedCount = getSelectedLoans().size();
        loanItemNo.setText(String.valueOf(selectedCount));
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
