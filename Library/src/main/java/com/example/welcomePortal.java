package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class welcomePortal {

    @FXML
    private Button aboutButton;

    @FXML
    private Button branchesButton;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private Button homeButton;

    @FXML
    private BorderPane imageHeader;

    @FXML
    private Button loginButton;

    @FXML
    private Text tabName;

    // Cache for loaded views to improve performance
    private final Map<String, Node> loadedViews = new HashMap<>();
    
    // Track current view to avoid unnecessary reloads
    private String currentView = "";

    @FXML
    private void initialize() {
        // Set up button handlers
        loginButton.setOnAction(event -> showLoginView());
        aboutButton.setOnAction(event -> showAboutView());
        homeButton.setOnAction(event -> showHomeView());
        branchesButton.setOnAction(event -> showBranchesView());
        
        // Load home view asynchronously to improve startup time
        javafx.application.Platform.runLater(this::showHomeView);
    }

    private void showLoginView() {
        if (!"login".equals(currentView)) {
            loadViewAsync("login", "fxml/welcomePage/login.fxml", "Login");
        }
    }
    
    private void showAboutView() {
        if (!"about".equals(currentView)) {
            loadViewAsync("about", "fxml/welcomePage/About.fxml", "About");
        }
    }
    
    private void showHomeView() {
        if (!"home".equals(currentView)) {
            loadViewAsync("home", "fxml/welcomePage/home.fxml", "Home");
        }
    }
    
    private void showBranchesView() {
        if (!"branches".equals(currentView)) {
            loadViewAsync("branches", "fxml/welcomePage/Branches.fxml", "Branches");
        }
    }
    
    // Optimized view loading with caching
    private void loadViewAsync(String viewName, String fxmlPath, String tabText) {
        // Check if view is already cached
        Node cachedView = loadedViews.get(viewName);
        if (cachedView != null) {
            contentArea.getChildren().setAll(cachedView);
            tabName.setText(tabText);
            currentView = viewName;
            System.out.println("Loaded " + viewName + " from cache");
            return;
        }
        
        // Load view asynchronously if not cached
        javafx.application.Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
                Node viewNode = loader.load();
                
                // Cache the loaded view
                loadedViews.put(viewName, viewNode);
                
                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    contentArea.getChildren().setAll(viewNode);
                    tabName.setText(tabText);
                    currentView = viewName;
                    System.out.println("Loaded and cached " + viewName);
                });
                
            } catch (IOException e) {
                System.err.println("Failed to load " + viewName + ": " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

}
