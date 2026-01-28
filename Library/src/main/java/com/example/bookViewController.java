package com.example;

import com.example.model.Book;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class bookViewController {

    @FXML
    private Button backButton;

    @FXML
    private Text bookAuthor;

    @FXML
    private Pane bookCover;

    @FXML
    private Text bookPublicationDate;

    @FXML
    private Text bookPublisher;

    @FXML
    private Text bookStatus;

    @FXML
    private Text bookSynopsis;
    
    @FXML
    private Text bookTitle;

    public void setBookData(Book book) {
        // Set title
        bookTitle.setText(book.getTitle());
        
        // Set author
        bookAuthor.setText(book.getAuthor());
        
        // Set publisher
        bookPublisher.setText(book.getPublisher());
        
        // Set publication date
        bookPublicationDate.setText(book.getPublicationDate().toString());
        
        // Set status
        bookStatus.setText(book.getStatus().toString());
        
        // Set synopsis
        bookSynopsis.setText(book.getSynopsis());
        
        // Set cover image if available
        String coverPath = book.getCoverImagePath();
        if (coverPath != null && !coverPath.isEmpty()) {
            try {
                Image coverImage = new Image(getClass().getResourceAsStream(coverPath));
                ImageView imageView = new ImageView(coverImage);
                imageView.setFitHeight(540);
                imageView.setFitWidth(440);
                imageView.setPreserveRatio(false);
                
                bookCover.getChildren().clear();
                bookCover.getChildren().add(imageView);
            } catch (Exception e) {
                // If image loading fails, keep default background
                System.err.println("Failed to load cover image: " + coverPath);
            }
        }
    }
    
    @FXML
    private void handleBackButton() {
        // Navigate back to the book collection using direct approach
        try {
            navigateBackToCollection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void navigateBackToCollection() {
        try {
            // Get the current scene and find the memberPortal controller
            Scene scene = backButton.getScene();
            if (scene != null) {
                // Try to find memberPortal controller first
                memberPortalController memberController = memberPortalController.getInstance();
                if (memberController != null) {
                    memberController.showBookCollection();
                    System.out.println("Back navigation via controller successful");
                } else {
                    // Fallback: direct navigation
                    System.out.println("Controller not found, using direct navigation");
                    // You might need to reload the books view here
                    // For now, let's try to trigger the showBooks method indirectly
                    triggerShowBooks();
                }
            }
        } catch (Exception e) {
            System.err.println("Back navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void triggerShowBooks() {
        try {
            // Find a way to trigger the books view reload
            // This might require access to the memberPortal controller or a refresh mechanism
            Scene scene = backButton.getScene();
            if (scene != null) {
                // Look for any controller that can handle showBooks
                javafx.scene.Parent root = scene.getRoot();
                memberPortalController controller = memberPortalController.getInstance();
                if (controller != null) {
                    controller.showBooks();
                    System.out.println("Direct showBooks trigger successful");
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to trigger showBooks: " + e.getMessage());
        }
    }
    
    private memberPortalController getMemberPortalController() {
        try {
            // Get the root of the current scene
            javafx.scene.Scene scene = backButton.getScene();
            if (scene != null && scene.getRoot() instanceof javafx.scene.layout.BorderPane) {
                javafx.scene.layout.BorderPane rootPane = (javafx.scene.layout.BorderPane) scene.getRoot();
                // Look for memberPortal in the scene graph
                return findMemberPortalController(rootPane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private memberPortalController findMemberPortalController(javafx.scene.Parent parent) {
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
}
