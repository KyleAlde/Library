package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

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
    private VBox sectionContainer;

    @FXML
    private BorderPane imageHeader;

    @FXML
    private AnchorPane sideBar;

    private List<sectionContainerController> sectionControllers = new ArrayList<>();

    @FXML
    private void initialize() {
        // Initially hide sidebar and remove from layout
        sideBar.setVisible(false);
        imageHeader.setLeft(null);
        
        // Load section containers with books
        loadSectionContainers();
        
        closeSideBar.setOnAction(event -> {
            sideBar.setVisible(false);
            imageHeader.setLeft(null);
            // Show logo and open button when sidebar is hidden
            headerLogo.setVisible(true);
            openSideBar.setVisible(true);
            
            // Sidebar closed: restore original layout
            adjustSectionsForSidebar(false);
        });
        
        openSideBar.setOnAction(event -> {
            sideBar.setVisible(true);
            imageHeader.setLeft(sideBar);
            // Hide logo and open button when sidebar is shown
            headerLogo.setVisible(false);
            openSideBar.setVisible(false);
            
            // Sidebar open: adjust layout for overflow
            adjustSectionsForSidebar(true);
        });
    }

    private void adjustSectionsForSidebar(boolean sidebarOpen) {
        // Apply width adjustment to all sections - HBox will handle centering
        for (sectionContainerController controller : sectionControllers) {
            if (sidebarOpen) {
                controller.adjustForSidebarOpen();
            } else {
                controller.adjustForSidebarClosed();
            }
        }
    }

    private void loadSectionContainers() {
        try {
            // Load multiple section containers
            String[] genres = {"Fiction", "Non-Fiction", "Science", "History"};
            
            for (String genre : genres) {
                // Create an HBox wrapper for centering
                // Create an HBox wrapper for centering
                javafx.scene.layout.HBox sectionWrapper = new javafx.scene.layout.HBox();
                sectionWrapper.setAlignment(javafx.geometry.Pos.CENTER);
                sectionWrapper.setPrefWidth(1920.0);
                sectionWrapper.setStyle("-fx-background-color: transparent;");
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Collection/sectionContainer.fxml"));
                VBox sectionNode = loader.load();
                
                // Get the controller and set the genre title
                sectionContainerController controller = loader.getController();
                controller.setGenreTitle(genre);
                
                // Store controller reference
                sectionControllers.add(controller);
                
                // Add section to wrapper, then wrapper to main container
                sectionWrapper.getChildren().add(sectionNode);
                sectionContainer.getChildren().add(sectionWrapper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
