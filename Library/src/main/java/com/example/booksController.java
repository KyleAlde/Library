package com.example;

import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class booksController {

    @FXML
    private VBox book;

    @FXML
    private Pane bookCover;

    @FXML
    private Text bookTitle;

    private Book currentBook;

    @FXML
    void loadBookView(MouseEvent event) {
        System.out.println("Book clicked! Current book: " + (currentBook != null ? currentBook.getTitle() : "null"));
        
        if (currentBook == null) {
            System.err.println("No book data available!");
            return;
        }
        
        try {
            // Load the bookView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/bookView.fxml"));
            Parent bookViewRoot = loader.load();
            
            // Get the controller and set book data
            bookViewController controller = loader.getController();
            controller.setBookData(currentBook);
            
            // Get the memberPortal controller using static instance
            memberPortalController memberController = memberPortalController.getInstance();
            if (memberController != null) {
                memberController.showBookView(bookViewRoot);
                System.out.println("Book view loaded successfully for: " + currentBook.getTitle());
            } else {
                System.err.println("Could not find memberPortal controller!");
                // Fallback: try to navigate using the scene directly
                navigateDirectly(bookViewRoot);
            }
            
        } catch (Exception e) {
            System.err.println("Error loading book view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void navigateDirectly(Parent bookViewRoot) {
        try {
            // Get the current scene and find the bookCollectionContainer
            Scene scene = book.getScene();
            if (scene != null) {
                // Look for the bookCollectionContainer in the scene graph
                VBox bookContainer = findBookCollectionContainer(scene.getRoot());
                if (bookContainer != null) {
                    bookContainer.getChildren().clear();
                    bookContainer.getChildren().add(bookViewRoot);
                    bookContainer.setPrefWidth(1420.0);
                    bookContainer.setMaxWidth(1420.0);
                    System.out.println("Direct navigation successful for: " + currentBook.getTitle());
                } else {
                    System.err.println("Could not find bookCollectionContainer!");
                }
            }
        } catch (Exception e) {
            System.err.println("Direct navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox findBookCollectionContainer(javafx.scene.Parent parent) {
        if (parent instanceof VBox && "bookCollectionContainer".equals(parent.getId())) {
            return (VBox) parent;
        }
        // Recursively search child nodes
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.Parent) {
                VBox container = findBookCollectionContainer((javafx.scene.Parent) node);
                if (container != null) {
                    return container;
                }
            }
        }
        return null;
    }
    
    private memberPortalController getMemberPortalController() {
        // Navigate up the scene graph to find the memberPortal controller
        try {
            // Get the root of the current scene
            Scene scene = book.getScene();
            if (scene != null && scene.getRoot() instanceof BorderPane) {
                BorderPane rootPane = (BorderPane) scene.getRoot();
                // Look for memberPortal in the scene graph
                return findMemberPortalController(rootPane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private memberPortalController findMemberPortalController(javafx.scene.Parent parent) {
        // This is a simplified approach - you may need to adjust based on your scene structure
        if (parent.getUserData() instanceof memberPortalController) {
            return (memberPortalController) parent.getUserData();
        }
        // Recursively search child nodes
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.Parent) {
                memberPortalController controller = findMemberPortalController((javafx.scene.Parent) node);
                if (controller != null) {
                    return controller;
                }
            }
        }
        return null;
    }

    public void setBookData(Book bookData) {
        this.currentBook = bookData;
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
