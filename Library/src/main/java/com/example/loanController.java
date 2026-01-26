package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class loanController {

    @FXML
    private Button addBookToCheckout;

    @FXML
    private TableColumn<?, ?> booPublication;

    @FXML
    private TableColumn<?, ?> bookAuthor;

    @FXML
    private Pane bookCover;

    @FXML
    private Pane bookCover1;

    @FXML
    private TableView<?> bookDetails;

    @FXML
    private TableView<?> bookDetails1;

    @FXML
    private TableColumn<?, ?> bookDueDate;

    @FXML
    private TableColumn<?, ?> bookPublication;

    @FXML
    private TableColumn<?, ?> bookTitle;

    @FXML
    private Button checkoutButton;

    @FXML
    private Pane filler;

}
