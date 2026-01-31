package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.utility.UserSession;

public class librarianPortalController {

    @FXML
    private Button closeSideBar;

    @FXML
    private VBox header;

    @FXML
    private BorderPane imageHeader;

    @FXML
    private Button openSideBar;

    @FXML
    private Button returnManageAccounts;

    @FXML
    private Button returnManageBooks;

    @FXML
    private Button returnManageRequests;

    @FXML
    private AnchorPane sideBar;
    
    @FXML
    private VBox contentContainer;
    
    // Page caching system for efficient layout management
    private Map<String, Node> loadedPages = new HashMap<>();
    
    // Constants for layout dimensions
    private static final double FULL_WIDTH = 1920.0;

    @FXML
    private void initialize() {
        // Validate user session
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn() || !session.isLibrarian()) {
            System.err.println("Unauthorized access to librarian portal - redirecting to login");
            // TODO: Navigate back to login screen
            return;
        }
        
        System.out.println("Librarian portal initialized for user: " + session.getUserId());
        
        // Initially hide sidebar
        sideBar.setVisible(false);
        imageHeader.setLeft(null);
        
        // Preload all pages for efficient navigation
        preloadPages();
        
        // Show initial page
        showInitialPage();
        
        // Setup sidebar toggle
        setupSidebarToggle();
    }
    
    private void setupSidebarToggle() {
        closeSideBar.setOnAction(event -> {
            sideBar.setVisible(false);
            imageHeader.setLeft(null);
        });
        
        openSideBar.setOnAction(event -> {
            sideBar.setVisible(true);
            imageHeader.setLeft(sideBar);
        });
    }
    
    // Preload all pages for efficient navigation
    private void preloadPages() {
        loadPageOnce("accounts", "LibrarianPage/manageAccounts.fxml");
        loadPageOnce("books", "LibrarianPage/manageBooks.fxml");
        // Add other pages when they exist
        // loadPageOnce("requests", "LibrarianPage/manageRequests.fxml");
    }
    
    // Load a page once and cache it
    private void loadPageOnce(String pageName, String fxmlPath) {
        try {
            HBox wrapper = new HBox();
            wrapper.setAlignment(javafx.geometry.Pos.CENTER);
            wrapper.setPrefWidth(FULL_WIDTH);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/" + fxmlPath));
            Node pageNode = loader.load();
            
            wrapper.getChildren().add(pageNode);
            loadedPages.put(pageName, wrapper);
            
            System.out.println("Successfully preloaded " + pageName + " page");
        } catch (Exception e) {
            System.err.println("Failed to preload " + pageName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Show the initial page (accounts)
    private void showInitialPage() {
        switchToPage("accounts");
    }
    
    // Efficient page switching using cached pages
    private void switchToPage(String pageName) {
        Node page = loadedPages.get(pageName);
        if (page != null) {
            contentContainer.getChildren().setAll(page);
            System.out.println("Switched to " + pageName + " page");
        } else {
            System.err.println("Page " + pageName + " not found in cache");
        }
    }

    @FXML
    void handleAccountsButton(ActionEvent event) {
        switchToPage("accounts");
    }

    @FXML
    void handleBooksButton(ActionEvent event) {
        switchToPage("books");
    }

    @FXML
    void handleRequestsButton(ActionEvent event) {
        // TODO: Implement when manageRequests.fxml exists
        System.out.println("Manage Requests - Coming Soon");
    }

}
