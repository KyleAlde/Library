package com.example;

import com.example.utility.dao.LoanDAO;
import com.example.utility.dao.BookDAO;
import com.example.model.Loan;
import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.List;

public class checkoutHistoryController {

    @FXML
    private Text accountBookAuthor;

    @FXML
    private Text accountBookCheckedoutDate;

    @FXML
    private Text accountBookTitle;

    @FXML
    private HBox accountCheckedoutHistory;

    private LoanDAO loanDAO;
    private BookDAO bookDAO;

    public void initialize() {
        loanDAO = new LoanDAO();
        bookDAO = new BookDAO();
    }

    // Simple method to set book data
    public void setBookData(String title, String author, String checkoutDate) {
        if (accountBookTitle != null) {
            accountBookTitle.setText(title != null ? title : "No title");
        }
        if (accountBookAuthor != null) {
            accountBookAuthor.setText(author != null ? author : "No author");
        }
        if (accountBookCheckedoutDate != null) {
            accountBookCheckedoutDate.setText(checkoutDate != null ? checkoutDate : "No date");
        }
    }

    // Load checkout history using existing DAO
    public void loadCheckoutHistory(String borrowerId) {
        try {
            System.out.println("DEBUG: Loading checkout history for borrower: " + borrowerId);
            
            // Get all loans for the borrower using existing DAO
            List<Loan> loans = loanDAO.getLoansByBorrower(borrowerId);
            System.out.println("DEBUG: Found " + loans.size() + " loans");
            
            if (loans.isEmpty()) {
                // Show no history message
                setBookData("No checkout history", "", "Start borrowing books to see history");
            } else {
                // Display each loan
                for (Loan loan : loans) {
                    displayLoan(loan);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading checkout history: " + e.getMessage());
            e.printStackTrace();
            setBookData("Error loading history", "Please try again", "Error");
        }
    }

    private void displayLoan(Loan loan) {
        try {
            // Get book details using existing DAO
            Book book = bookDAO.getBook(loan.getBookId());
            
            if (book != null) {
                setBookData(book.getTitle(), book.getAuthor(), loan.getLoanDate().toLocalDate().toString());
            } else {
                setBookData("Unknown Book", "Unknown Author", loan.getLoanDate().toLocalDate().toString());
            }
            
        } catch (Exception e) {
            System.err.println("Error displaying loan: " + e.getMessage());
            setBookData("Error", "", loan.getLoanDate().toLocalDate().toString());
        }
    }

    // Getter for the HBox container
    public HBox getAccountCheckedoutHistory() {
        return accountCheckedoutHistory;
    }
}
