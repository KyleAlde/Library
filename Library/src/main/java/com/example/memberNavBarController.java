package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class memberNavBarController {

    @FXML
    private Button aboutButton;

    @FXML
    private Button branchesButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button loginButton;

    @FXML
    private MenuButton accountMenuButton;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private StackPane nestedUI;

    @FXML
    private Text pageTitle;

    @FXML
    public void initialize() {
        try {
            pageTitle.setText("Collections");
            AnchorPane nestedHome = FXMLLoader.load(getClass().getResource("/com/example/fxml/nestedMemberHome.fxml"));
            nestedUI.getChildren().add(nestedHome);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void returnAbout(ActionEvent event) {
        try {
            pageTitle.setText("About");
            nestedUI.getChildren().clear();
            AnchorPane aboutPane = FXMLLoader.load(getClass().getResource("/com/example/fxml/nestedAbout.fxml"));
            nestedUI.getChildren().add(aboutPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void returnBranches(ActionEvent event) {
        try {
            pageTitle.setText("Branches");
            nestedUI.getChildren().clear();
            AnchorPane branchesPane = FXMLLoader.load(getClass().getResource("/com/example/fxml/nestedBranches.fxml"));
            nestedUI.getChildren().add(branchesPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void returnHome(ActionEvent event) {
        try {
            pageTitle.setText("Collections");
            nestedUI.getChildren().clear();
            AnchorPane homePane = FXMLLoader.load(getClass().getResource("/com/example/fxml/nestedMemberHome.fxml"));
            nestedUI.getChildren().add(homePane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void returnLogin(ActionEvent event) {
        try {
            App.setRoot("fxml/PlaceholderUI");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            App.setRoot("fxml/PlaceholderUI");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
