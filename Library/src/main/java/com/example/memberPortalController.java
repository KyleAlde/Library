package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.HashMap;
import java.util.Map;

public class memberPortalController {

    // Static reference for global access
    private static memberPortalController instance;
    
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
    private VBox bookCollectionContainer;

    @FXML
    private BorderPane imageHeader;

    @FXML
    private AnchorPane sideBar;

    private sectionContainerController sectionController;
    
    // Page caching system for efficient layout management
    private Map<String, Node> loadedPages = new HashMap<>();
    
    // Constants for layout dimensions
    private static final double FULL_WIDTH = 1920.0;

    // Static getter for global access
    public static memberPortalController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        // Set static instance for global access
        instance = this;
        
        // Initially hide sidebar and remove from layout
        sideBar.setVisible(false);
        imageHeader.setLeft(null);
        
        // Preload all pages for efficient navigation
        preloadPages();
        
        // Show initial page
        showInitialPage();
        
        // Set initial width to ensure proper centering
        javafx.application.Platform.runLater(() -> {
            bookCollectionContainer.setPrefWidth(FULL_WIDTH);
            bookCollectionContainer.setMaxWidth(FULL_WIDTH);
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

    // Preload all pages for efficient navigation
    private void preloadPages() {
        loadPageOnce("books", "fxml/Collection/sectionContainer.fxml");
        loadPageOnce("cart", "fxml/Cart.fxml");
        loadPageOnce("loans", "fxml/Loans.fxml");
        loadPageOnce("account", "fxml/account.fxml");
    }
    
    // Load a page once and cache it
    private void loadPageOnce(String pageName, String fxmlPath) {
        try {
            HBox wrapper = new HBox();
            wrapper.setAlignment(javafx.geometry.Pos.CENTER);
            wrapper.setPrefWidth(FULL_WIDTH);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node pageNode = loader.load();
            
            // Store the controller reference for books page
            if ("books".equals(pageName)) {
                sectionController = loader.getController();
                sectionController.setGenreTitle("All Books");
            }
            
            wrapper.getChildren().add(pageNode);
            loadedPages.put(pageName, wrapper);
            
            System.out.println("Successfully preloaded " + pageName + " page");
        } catch (Exception e) {
            System.err.println("Failed to preload " + pageName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Show the initial page (books)
    private void showInitialPage() {
        switchToPage("books");
    }
    
    // Efficient page switching using cached pages
    private void switchToPage(String pageName) {
        Node page = loadedPages.get(pageName);
        if (page != null) {
            bookCollectionContainer.getChildren().setAll(page);
            System.out.println("Switched to " + pageName + " page");
        } else {
            System.err.println("Page " + pageName + " not found in cache");
        }
    }

    // Method to show cart page
    public void showCart() {
        switchToPage("cart");
    }

    // Method to show books page (Browse)
    public void showBooks() {
        switchToPage("books");
    }

    // Method to show loans page
    public void showLoans() {
        switchToPage("loans");
    }

    // Method to show account page
    public void showAccount() {
        switchToPage("account");
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

    @FXML
    private void handleLoansButton() {
        showLoans();
    }

    @FXML
    private void handleAccountButton() {
        showAccount();
    }

    // Navigation button event handlers
    @FXML
    private void handleReturnBrowse() {
        showBooks();
    }

    @FXML
    private void handleReturnCart() {
        showCart();
    }

    @FXML
    private void handleReturnLoans() {
        showLoans();
    }

    @FXML
    private void handleReturnAccount() {
        showAccount();
    }

    // Book view navigation methods
    public void showBookView(javafx.scene.Parent bookViewRoot) {
        // Create a wrapper for the book view to match the pattern used by other pages
        VBox wrapper = new VBox(bookViewRoot);
        wrapper.setPrefWidth(1420.0);
        wrapper.setMaxWidth(1420.0);
        
        // Use the same switching logic as other scenes
        bookCollectionContainer.getChildren().setAll(wrapper);
        System.out.println("Switched to book view");
    }
    
    public void showBookCollection() {
        // Return to the normal book collection view using the same pattern
        showBooks();
    }

}
