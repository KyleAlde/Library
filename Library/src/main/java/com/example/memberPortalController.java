package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.example.utility.UserSession;
import com.example.model.Book;
import com.example.utility.dao.CartDAO;
import com.example.utility.dao.BorrowRequestDAO;

public class memberPortalController {

    // Static reference for global access
    private static memberPortalController instance;
    
    // Cart management
    private CartDAO cartDAO;
    private BorrowRequestDAO borrowRequestDAO;
    private cartController cartController;
    
    @FXML
    private Button closeSideBar;

    @FXML
    private VBox header;

    @FXML
    private Button headerLogo;

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

    @FXML
    private TextField searchBar;

    private sectionContainerController sectionController;
    private displayCatalogueController catalogueController;
    
    // Page caching system for efficient layout management
    private Map<String, Node> loadedPages = new HashMap<>();
    
    // Constants for layout dimensions
    private static final double FULL_WIDTH = 1920.0;

    // Static getter for global access
    public static memberPortalController getInstance() {
        return instance;
    }

    public memberPortalController() {
    // CartDAO will be initialized in initialize()
}

    @FXML
    private void initialize() {
        // Validate user session
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn() || !"borrower".equals(session.getUserType())) {
            System.err.println("Unauthorized access attempt to member portal");
            // TODO: Redirect to login screen
            return;
        }
        
        // Set static instance for global access
        instance = this;
        
        // Initialize CartDAO
        cartDAO = new CartDAO();
        borrowRequestDAO = new BorrowRequestDAO();
        
        System.out.println("Member portal controller initialized for user: " + session.getUserName());
        
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

        if (searchBar != null) {
            searchBar.setOnAction(event -> handleSearch());
        }
    }

    @FXML
    private void handleSearch() {
        String searchQuery = searchBar.getText().trim();
        
        // Switch to catalogue page first
        showCatalogue();
        
        // Use the stored catalogue controller to perform search
        if (catalogueController != null) {
            catalogueController.performSearch(searchQuery);
            System.out.println("Searching for: \"" + searchQuery + "\"");
        } else {
            System.err.println("Catalogue controller not available");
        }
    }

    // Preload all pages for efficient navigation
    private void preloadPages() {
        loadPageOnce("books", "fxml/Collection/sectionContainer.fxml");
        loadPageOnce("catalogue", "fxml/displayCatalogue.fxml");
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
                sectionController.setGenreTitle("Catalog");
            }
            
            // Store the controller reference for catalogue page
            if ("catalogue".equals(pageName)) {
                catalogueController = loader.getController();
                // Preload catalogue data in background for faster initial display
                javafx.application.Platform.runLater(() -> {
                    System.out.println("Preloading catalogue data...");
                    catalogueController.refreshCatalogue();
                });
            }
            
            // Store the controller reference for cart page
            if ("cart".equals(pageName)) {
                cartController = loader.getController();
                // Update cart display with current items
                updateCartDisplay();
            }
            
            wrapper.getChildren().add(pageNode);
            loadedPages.put(pageName, wrapper);
            
            System.out.println("Successfully preloaded " + pageName + " page");
        } catch (Exception e) {
            System.err.println("Failed to preload " + pageName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Show the initial page (catalogue)
    private void showInitialPage() {
        switchToPage("catalogue");
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

    // Method to show catalogue page
    public void showCatalogue() {
        switchToPage("catalogue");
        // Refresh catalogue to show full catalogue instead of search results
        if (catalogueController != null) {
            catalogueController.refreshCatalogue();
        }
    }

    // Method to show books page (Browse)
    public void showBooks() {
        switchToPage("books");
        // Refresh catalogue to show full catalogue instead of search results
        if (catalogueController != null) {
            catalogueController.refreshCatalogue();
        }
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
    private void handleHeaderLogoClick() {
        showCatalogue();
    }

    @FXML
    private void handleBrowseButton() {
        showCatalogue();
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

    // Cart management methods
    public void addToCart(Book book) {
        if (book == null) return;
        
        // Get current user ID from session
        UserSession session = UserSession.getInstance();
        String userId = session.getUserId();
        
        // Check if book is already in cart using database
        if (cartDAO.isBookInCart(userId, book.getIsbn())) {
            System.out.println("Book already in cart: " + book.getTitle());
            return;
        }
        
        // Add to cart using database
        if (cartDAO.addToCart(userId, book.getIsbn())) {
            System.out.println("Added to cart: " + book.getTitle());
            updateCartDisplay();
        } else {
            System.err.println("Failed to add book to cart: " + book.getTitle());
        }
    }
    
    public void removeFromCart(Book book) {
        // Get current user ID from session
        UserSession session = UserSession.getInstance();
        String userId = session.getUserId();
        
        // Remove from cart using database
        if (cartDAO.removeFromCart(userId, book.getIsbn())) {
            System.out.println("Removed from cart: " + book.getTitle());
            updateCartDisplay();
        } else {
            System.err.println("Failed to remove book from cart: " + book.getTitle());
        }
    }
    
    public void updateCartDisplay() {
        if (cartController != null) {
            // Get cart items from database
            UserSession session = UserSession.getInstance();
            String userId = session.getUserId();
            List<Book> cartItems = cartDAO.getCartItems(userId);
            cartController.updateCartItems(cartItems);
        }
    }
    
    public List<Book> getCartItems() {
        // Get cart items from database
        UserSession session = UserSession.getInstance();
        String userId = session.getUserId();
        return cartDAO.getCartItems(userId);
    }
    
    public void clearCart() {
        // Get current user ID from session
        UserSession session = UserSession.getInstance();
        String userId = session.getUserId();
        
        // Get current cart items before clearing
        List<Book> cartItems = cartDAO.getCartItems(userId);
        
        if (cartItems.isEmpty()) {
            System.out.println("Cart is already empty");
            return;
        }
        
        // Create borrow requests for all cart items (checkout process)
        List<String> bookIsbns = new ArrayList<>();
        for (Book book : cartItems) {
            bookIsbns.add(book.getIsbn());
        }
        
        // Create borrow requests
        boolean requestsCreated = borrowRequestDAO.createBorrowRequests(bookIsbns, userId);
        
        if (requestsCreated) {
            System.out.println("Created " + bookIsbns.size() + " borrow requests for checkout");
            
            // Clear the cart after successful borrow request creation
            if (cartDAO.clearCart(userId)) {
                System.out.println("Cart cleared successfully after checkout");
                updateCartDisplay();
            } else {
                System.err.println("Failed to clear cart after checkout");
            }
        } else {
            System.err.println("Failed to create borrow requests during checkout");
        }
    }
    
    // Add a separate method for just clearing cart without creating borrow requests
    public void clearCartWithoutCheckout() {
        UserSession session = UserSession.getInstance();
        String userId = session.getUserId();
        
        if (cartDAO.clearCart(userId)) {
            System.out.println("Cart cleared");
            updateCartDisplay();
        } else {
            System.err.println("Failed to clear cart");
        }
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
        System.out.println("showBookCollection() called in memberPortalController");
        
        // Use the proper page switching system instead of direct content restoration
        // This ensures consistent behavior with other navigation methods
        showCatalogue();
        
        System.out.println("Used showCatalogue() for proper page switching");
    }

}
