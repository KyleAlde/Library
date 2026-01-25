package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class sectionContainerController {

    @FXML
    private FlowPane booksFlowPane;

    @FXML
    private Text genreTitle;

    @FXML
    private HBox sectionHeader;

    @FXML
    private void initialize() {
        // Load 10 placeholder books
        loadPlaceholderBooks();
    }

    private void loadPlaceholderBooks() {
        try {
            // Load 10 placeholder books
            for (int i = 0; i < 10; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Collection/books.fxml"));
                VBox bookNode = loader.load();
                booksFlowPane.getChildren().add(bookNode);
            }
        } catch (Exception e) {
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

    // Method for when sidebar is CLOSED - restore bookCollectionContainer to 1180px width
    public void adjustForSidebarClosed(VBox bookCollectionContainer) {
        // Set bookCollectionContainer width back to 1180px for sidebar closed state
        bookCollectionContainer.setPrefWidth(1180.0);
        bookCollectionContainer.setMaxWidth(1180.0);
        
        // Force layout recalculation
        bookCollectionContainer.requestLayout();
    }
}