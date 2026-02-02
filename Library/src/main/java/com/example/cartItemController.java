package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import com.example.model.Book;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

public class cartItemController {

    @FXML
    private Text bookAuthor;

    @FXML
    private Pane bookCover;

    @FXML
    private Text bookPublisher;

    @FXML
    private Text bookTitle;

    @FXML
    private HBox cartItemContainer;

    @FXML
    private Button removeBookfromCheckout;
    
    private Book currentBook;
    private memberPortalController memberController;

    public void setBookData(Book book) {
        this.currentBook = book;
        
        // Set book information
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookPublisher.setText(book.getPublisher());
        
        // Set cover image if available
        String coverPath = book.getCoverImagePath();
        if (coverPath != null && !coverPath.isEmpty()) {
            try {
                Image coverImage = new Image(getClass().getResourceAsStream(coverPath));
                ImageView imageView = new ImageView(coverImage);
                imageView.setFitHeight(200); // Max height
                imageView.setFitWidth(160);  // Max width
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                bookCover.getChildren().clear();
                bookCover.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Failed to load cover image: " + coverPath);
            }
        }
        
        // Get member portal controller for cart operations
        memberController = memberPortalController.getInstance();
    }
    
    @FXML
    private void removeBookfromCheckout(ActionEvent event) {
        if (currentBook != null && memberController != null) {
            memberController.removeFromCart(currentBook);
        }
    }
}
