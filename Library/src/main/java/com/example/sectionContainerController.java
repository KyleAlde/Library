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
        // Load 16 books into the FlowPane
        loadBooks();
    }

    private void loadBooks() {
        try {
            // Load 16 books
            for (int i = 0; i < 16; i++) {
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

    // Method for when sidebar is OPEN - reduce width for 5 books per row
    public void adjustForSidebarOpen() {
        // Set width to fit 5 books per row (6th goes to next row)
        booksFlowPane.setMaxWidth(1180.0);  // 5 books × 220px + 4 gaps × 20px = 1180px + padding
        booksFlowPane.setPrefWidth(1180.0);
        
        // Force layout recalculation
        booksFlowPane.requestLayout();
    }

    // Method for when sidebar is CLOSED - full width for 6 books per row
    public void adjustForSidebarClosed() {
        // Set width for 6 books per row
        booksFlowPane.setMaxWidth(1420.0);  // 6 books × 220px + 5 gaps × 20px = 1420px
        booksFlowPane.setPrefWidth(1420.0);
        
        // Force layout recalculation
        booksFlowPane.requestLayout();
    }
}
