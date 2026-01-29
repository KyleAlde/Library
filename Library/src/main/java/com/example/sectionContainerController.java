package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.example.utility.dao.BookDAO;
import com.example.model.Book;
import java.util.List;

public class sectionContainerController {

    @FXML
    private FlowPane booksFlowPane;

    @FXML
    private Text genreTitle;

    @FXML
    private HBox sectionHeader;

    private final BookDAO bookDAO = new BookDAO();

    @FXML
    private void initialize() {
        // Load books from database
        loadAllBooks();
    }

    public void loadAllBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            displayBooks(books);
            genreTitle.setText("All Books");
            System.out.println("Loaded " + books.size() + " books from database");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading books from database: " + e.getMessage());
        }
    }

    public void searchBooks(String searchQuery) {
        try {
            List<Book> searchResults = bookDAO.searchBooks(searchQuery);
            displayBooks(searchResults);
            genreTitle.setText("Search Results: " + searchQuery);
            System.out.println("Found " + searchResults.size() + " books matching: " + searchQuery);
        } catch (Exception e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void displayBooks(List<Book> books) {
        booksFlowPane.getChildren().clear();
        
        try {
            for (Book book : books) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/Collection/books.fxml"));
                VBox bookNode = loader.load();
                
                // Get the books controller and set book data
                booksController controller = loader.getController();
                controller.setBookData(book);
                
                booksFlowPane.getChildren().add(bookNode);
            }
            System.out.println("Displayed " + books.size() + " books");
        } catch (Exception e) {
            System.err.println("Error displaying books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setGenreTitle(String title) {
        genreTitle.setText(title);
    }

    // Method for when sidebar is OPEN - resize bookCollectionContainer to 1620px width
    public void adjustForSidebarOpen(VBox bookCollectionContainer) {
        // Set bookCollectionContainer width to 1620px for sidebar open state
        bookCollectionContainer.setPrefWidth(1620.0);
        bookCollectionContainer.setMaxWidth(1620.0);
        
        // Force layout recalculation
        bookCollectionContainer.requestLayout();
    }

    // Method for when sidebar is CLOSED - restore bookCollectionContainer to 1920px width
    public void adjustForSidebarClosed(VBox bookCollectionContainer) {
        // Set bookCollectionContainer width back to 1920px for sidebar closed state
        bookCollectionContainer.setPrefWidth(1920.0);
        bookCollectionContainer.setMaxWidth(1920.0);
        
        // Force layout recalculation
        bookCollectionContainer.requestLayout();
    }
}