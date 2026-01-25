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
        
        // Debug: Log initial width
        javafx.application.Platform.runLater(() -> {
            System.out.println("DEBUG - Initial bookCollectionContainer width: " + bookCollectionContainer.getWidth());
            System.out.println("DEBUG - Initial bookCollectionContainer prefWidth: " + bookCollectionContainer.getPrefWidth());
            System.out.println("DEBUG - Initial bookCollectionContainer bounds: " + bookCollectionContainer.getBoundsInParent());
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
            sectionController.setGenreTitle("hihi haha");
            
            // Add section to wrapper, then wrapper to main container
            sectionWrapper.getChildren().add(sectionNode);
            bookCollectionContainer.getChildren().add(sectionWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
