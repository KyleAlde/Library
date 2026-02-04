package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.example.model.Book;
import java.util.List;
import java.util.ArrayList;
import javafx.event.ActionEvent;

public class cartController {

    @FXML
    private Button cartCheckout;

    @FXML
    private VBox cartItemContainer;

    @FXML
    private Text itemNo;
    
    private List<Book> currentCartItems;

    public void updateCartItems(List<Book> cartItems) {
        this.currentCartItems = new ArrayList<>(cartItems);
        
        // Clear existing items
        cartItemContainer.getChildren().clear();
        
        // Update item count
        itemNo.setText(String.valueOf(cartItems.size()));
        
        // Add cart items
        for (Book book : cartItems) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fxml/cartItem.fxml"));
                AnchorPane cartItemNode = loader.load();
                
                cartItemController itemController = loader.getController();
                itemController.setBookData(book);
                
                cartItemContainer.getChildren().add(cartItemNode);
            } catch (Exception e) {
                System.err.println("Error loading cart item: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Cart updated with " + cartItems.size() + " items");
    }
    
    @FXML
    private void handleCheckout(ActionEvent event) {
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            System.out.println("Cart is empty, cannot checkout");
            return;
        }
        
        System.out.println("Processing checkout for " + currentCartItems.size() + " items:");
        for (Book book : currentCartItems) {
            System.out.println("- " + book.getTitle() + " by " + book.getAuthor());
        }
        
        // Clear cart after checkout - this creates borrow requests
        memberPortalController memberController = memberPortalController.getInstance();
        if (memberController != null) {
            try {
                memberController.clearCart();
                System.out.println("Checkout completed - borrow requests created and cart cleared");
                
                // Clear local cart items to refresh display
                currentCartItems.clear();
                cartItemContainer.getChildren().clear();
                itemNo.setText("0");
                
            } catch (Exception e) {
                System.err.println("Error during checkout: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
