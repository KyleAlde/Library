package com.example;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> showLoginView());
        aboutButton.setOnAction(event -> showAboutView());
        homeButton.setOnAction(event -> showHomeView());
        branchesButton.setOnAction(event -> showBranchesView());
        showHomeView(); // Load home view on startup
    }

    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/welcomePage/login.fxml"));
            Node loginView = loader.load();
            contentArea.getChildren().setAll(loginView);
            tabName.setText("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAboutView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/welcomePage/About.fxml"));
            Node aboutView = loader.load();
            contentArea.getChildren().setAll(aboutView);
            tabName.setText("About");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showHomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/welcomePage/home.fxml"));
            Node homeView = loader.load();
            contentArea.getChildren().setAll(homeView);
            tabName.setText("Home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showBranchesView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/welcomePage/Branches.fxml"));
            Node branchesView = loader.load();
            contentArea.getChildren().setAll(branchesView);
            tabName.setText("Branches");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
