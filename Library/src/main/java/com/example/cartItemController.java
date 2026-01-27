package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class cartItemController {

    @FXML
    private Text bookAuthor;

    @FXML
    private Pane bookCover;

    @FXML
    private Text bookPublisher;

    @FXML
    private Text bookTitle;

    @FXML
    private HBox cartItemContainer;

    @FXML
    private Button removeBookfromCheckout;

}
