package com.example;

import com.example.model.Book;
import javafx.fxml.FXML;
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
        // You can add more book data display here as needed
        // For example, setting book cover color based on genre, etc.
    }
}
