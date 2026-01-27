package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class manageBooksController {

    @FXML
    private Button addUser;

    @FXML
    private TableColumn<?, ?> age;

    @FXML
    private TableView<?> borrowerList;

    @FXML
    private Button deleteUser;

    @FXML
    private Button editUser;

    @FXML
    private TableColumn<?, ?> email;

    @FXML
    private TableColumn<?, ?> first_name;

    @FXML
    private TableColumn<?, ?> id;

    @FXML
    private TextField insertAge;

    @FXML
    private TextField insertEmail;

    @FXML
    private TextField insertFirstName;

    @FXML
    private TextField insertID;

    @FXML
    private TextField insertLastName;

    @FXML
    private TextField insertPassword;

    @FXML
    private TextField insertType;

    @FXML
    private TableColumn<?, ?> last_name;

    @FXML
    private TableColumn<?, ?> password;

    @FXML
    private TableColumn<?, ?> type;

}
