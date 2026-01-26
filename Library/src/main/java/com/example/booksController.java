package com.example;

import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class booksController {

    @FXML
    private VBox book;

    @FXML
    private Pane bookCover;

    @FXML
    private Text bookTitle;

    public void setBookData(Book bookData) {
        bookTitle.setText(bookData.getTitle());
        
        // Set cover image if available, otherwise keep black background
        String coverPath = bookData.getCoverImagePath();
        if (coverPath != null && !coverPath.isEmpty()) {
            try {
                Image coverImage = new Image(getClass().getResourceAsStream(coverPath));
                ImageView imageView = new ImageView(coverImage);
                imageView.setFitHeight(300);
                imageView.setFitWidth(220);
                imageView.setPreserveRatio(false);
                
                bookCover.getChildren().clear();
                bookCover.getChildren().add(imageView);
            } catch (Exception e) {
                // If image loading fails, keep black background
                System.err.println("Failed to load cover image: " + coverPath);
            }
        }
    }
}
