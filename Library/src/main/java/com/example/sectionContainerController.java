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

    @FXML
    private void initialize() {
        // Load books from database
        loadBooksFromDatabase();
    }

    private void loadBooksFromDatabase() {
        try {
            BookDAO bookDAO = new BookDAO();
            List<Book> books = bookDAO.getAllBooks();
            
            for (Book book : books) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Collection/books.fxml"));
                VBox bookNode = loader.load();
                
                // Get the books controller and set book data
                booksController controller = loader.getController();
                controller.setBookData(book);
                
                booksFlowPane.getChildren().add(bookNode);
            }
            
            System.out.println("Loaded " + books.size() + " books from database");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading books from database: " + e.getMessage());
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