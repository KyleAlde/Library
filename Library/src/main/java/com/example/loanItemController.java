package com.example;

import com.example.model.Loan;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class loanItemController {

    @FXML
    private Text bookAuthor;

    @FXML
    private Text bookPublisher;

    @FXML
    private Text bookTitle;

    @FXML
    private HBox cartItemContainer;

    @FXML
    private Text dueDate;

    @FXML
    private CheckBox returnItem;

    private Loan currentLoan;
    private loanController parentController;

    @FXML
    private void initialize() {
        // Setup checkbox listener
        returnItem.setOnAction(event -> {
            if (parentController != null) {
                parentController.updateItemCount();
            }
        });
    }

    public void setLoanData(Loan loan, Book book) {
        this.currentLoan = loan;
        
        // Set book information
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookPublisher.setText(book.getPublisher());
        
        // Set due date
        if (loan.getDueDate() != null) {
            dueDate.setText(loan.getDueDate().toString());
        } else {
            dueDate.setText("No due date");
        }
        
        // Reset checkbox
        returnItem.setSelected(false);
    }

    public void setParentController(loanController parentController) {
        this.parentController = parentController;
    }

    public boolean isSelected() {
        return returnItem.isSelected();
    }

    public Loan getLoan() {
        return currentLoan;
    }

    public void setSelected(boolean selected) {
        returnItem.setSelected(selected);
    }
}
