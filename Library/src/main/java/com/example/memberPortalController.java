package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class memberPortalController {

    @FXML
    private Button closeSideBar;

    @FXML
    private VBox header;

    @FXML
    private Pane headerLogo;

    @FXML
    private Button openSideBar;

    @FXML
    private Button returnAccount;

    @FXML
    private Button returnBrowse;

    @FXML
    private Button returnCart;

    @FXML
    private Button returnLoans;

    @FXML
    private Button returnReservations;

    @FXML
    private Button returnReturns;

    @FXML
    private VBox bookCollectionContainer;

    @FXML
    private BorderPane imageHeader;

    @FXML
    private AnchorPane sideBar;

    private sectionContainerController sectionController;

    @FXML
    private void initialize() {
        // Initially hide sidebar and remove from layout
        sideBar.setVisible(false);
        imageHeader.setLeft(null);
        
        // Load section containers with books
        loadSectionContainers();
        
        // Set initial width to ensure proper centering
        javafx.application.Platform.runLater(() -> {
            bookCollectionContainer.setPrefWidth(1920.0);
            bookCollectionContainer.setMaxWidth(1920.0);
            bookCollectionContainer.requestLayout();
        });
        
        closeSideBar.setOnAction(event -> {
            sideBar.setVisible(false);
            imageHeader.setLeft(null);
            // Show logo and open button when sidebar is hidden
            headerLogo.setVisible(true);
            openSideBar.setVisible(true);
            
            // Debug: Log width when sidebar closed
            javafx.application.Platform.runLater(() -> {
                System.out.println("DEBUG - Sidebar CLOSED - bookCollectionContainer width: " + bookCollectionContainer.getWidth());
                System.out.println("DEBUG - Sidebar CLOSED - bookCollectionContainer prefWidth: " + bookCollectionContainer.getPrefWidth());
                System.out.println("DEBUG - Sidebar CLOSED - bookCollectionContainer bounds: " + bookCollectionContainer.getBoundsInParent());
            });
            
            // Sidebar closed: restore original layout
            sectionController.adjustForSidebarClosed(bookCollectionContainer);
        });
        
        openSideBar.setOnAction(event -> {
            sideBar.setVisible(true);
            imageHeader.setLeft(sideBar);
            // Hide logo and open button when sidebar is shown
            headerLogo.setVisible(false);
            openSideBar.setVisible(false);
            
            // Debug: Log width when sidebar opened
            javafx.application.Platform.runLater(() -> {
                System.out.println("DEBUG - Sidebar OPENED - bookCollectionContainer width: " + bookCollectionContainer.getWidth());
                System.out.println("DEBUG - Sidebar OPENED - bookCollectionContainer prefWidth: " + bookCollectionContainer.getPrefWidth());
                System.out.println("DEBUG - Sidebar OPENED - bookCollectionContainer bounds: " + bookCollectionContainer.getBoundsInParent());
            });
            
            // Sidebar open: adjust layout for overflow
            sectionController.adjustForSidebarOpen(bookCollectionContainer);
        });
    }

    private void loadSectionContainers() {
        try {
            // Load single section container for all books
            javafx.scene.layout.HBox sectionWrapper = new javafx.scene.layout.HBox();
            sectionWrapper.setAlignment(javafx.geometry.Pos.CENTER);
            sectionWrapper.setPrefWidth(1920.0);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Collection/sectionContainer.fxml"));
            VBox sectionNode = loader.load();
            
            // Get the controller and set the genre title
            sectionController = loader.getController();
            sectionController.setGenreTitle("All Books");
            
            // Add section to wrapper, then wrapper to main container
            sectionWrapper.getChildren().add(sectionNode);
            bookCollectionContainer.getChildren().add(sectionWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to show cart page
    public void showCart() {
        try {
            // Clear current content
            bookCollectionContainer.getChildren().clear();
            
            // Create HBox wrapper for centering
            javafx.scene.layout.HBox cartWrapper = new javafx.scene.layout.HBox();
            cartWrapper.setAlignment(javafx.geometry.Pos.CENTER);
            cartWrapper.setPrefWidth(1920.0);
            
            // Load cart.fxml
            FXMLLoader cartLoader = new FXMLLoader(getClass().getResource("fxml/Cart.fxml"));
            javafx.scene.layout.AnchorPane cartNode = cartLoader.load();
            
            // Add cart to wrapper, then wrapper to main container
            cartWrapper.getChildren().add(cartNode);
            bookCollectionContainer.getChildren().add(cartWrapper);
            
            System.out.println("Cart page loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading cart page: " + e.getMessage());
        }
    }

    // Method to show books page (Browse)
    public void showBooks() {
        try {
            // Clear current content
            bookCollectionContainer.getChildren().clear();
            
            // Reload books
            loadSectionContainers();
            
            System.out.println("Books page loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading books page: " + e.getMessage());
        }
    }

    // Action handlers for sidebar buttons
    @FXML
    private void handleBrowseButton() {
        showBooks();
    }

    @FXML
    private void handleCartButton() {
        showCart();
    }

}
