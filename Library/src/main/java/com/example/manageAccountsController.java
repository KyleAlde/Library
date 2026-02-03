package com.example;

import com.example.model.Borrower;
import com.example.model.Borrower.BorrowerType;
import com.example.utility.dao.BorrowerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class manageAccountsController implements Initializable {

    @FXML
    private TableView<Borrower> borrowerList;
    
    @FXML
    private TableColumn<Borrower, String> id;
    
    @FXML
    private TableColumn<Borrower, String> last_name;
    
    @FXML
    private TableColumn<Borrower, String> first_name;
    
    @FXML
    private TableColumn<Borrower, Integer> age;
    
    @FXML
    private TableColumn<Borrower, String> type;
    
    @FXML
    private TableColumn<Borrower, String> email;
    
    @FXML
    private TableColumn<Borrower, String> password;
    
    @FXML
    private Button addUser;

    @FXML
    private Button deleteUser;

    @FXML
    private Button editUser;
    
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
    private ComboBox<String> insertType;
    
    private BorrowerDAO borrowerDAO;
    private ObservableList<Borrower> borrowerData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        borrowerDAO = new BorrowerDAO();
        setupTableColumns();
        setupTypeComboBox();
        loadBorrowerData();
        setupButtonHandlers();
    }
    
    private void setupTableColumns() {
        // Setup column mappings
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        last_name.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        first_name.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        age.setCellValueFactory(new PropertyValueFactory<>("age"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        
        // Set column header font to Segoe UI size 18
        id.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        last_name.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        first_name.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        age.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        type.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        email.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        password.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px;");
        
        // Set row font to Segoe UI size 16
        borrowerList.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px;");
    }
    
    private void setupTypeComboBox() {
        // Use values from BorrowerType enum for consistency
        for (BorrowerType type : BorrowerType.values()) {
            insertType.getItems().add(type.toString());
        }
        insertType.setValue(BorrowerType.STUDENT.toString()); // Set default value
    }
    
    private void setupButtonHandlers() {
        addUser.setOnAction(event -> handleAddUser());
        editUser.setOnAction(event -> handleEditUser());
        deleteUser.setOnAction(event -> handleDeleteUser());
        
        // Handle table selection to populate fields
        borrowerList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            });
    }
    
    private void populateFields(Borrower borrower) {
        insertID.setText(borrower.getId());
        insertLastName.setText(borrower.getLastName());
        insertFirstName.setText(borrower.getFirstName());
        insertAge.setText(String.valueOf(borrower.getAge()));
        insertType.setValue(borrower.getType().toString());
        insertEmail.setText(borrower.getEmail());
        insertPassword.setText(borrower.getPassword());
    }
    
    private void clearFields() {
        insertID.clear();
        insertLastName.clear();
        insertFirstName.clear();
        insertAge.clear();
        insertType.setValue(null);
        insertEmail.clear();
        insertPassword.clear();
    }
    
    private void loadBorrowerData() {
        try {
            List<Borrower> borrowers = borrowerDAO.getAllBorrowers();
            borrowerData = FXCollections.observableArrayList(borrowers);
            borrowerList.setItems(borrowerData);
            
            System.out.println("Loaded " + borrowers.size() + " borrowers into table");
        } catch (SQLException e) {
            System.err.println("Error loading borrower data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load borrower data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAddUser() {
        try {
            // Validate input fields
            if (!validateInput()) {
                return;
            }
            
            // Create new borrower
            String lastName = insertLastName.getText().trim();
            String firstName = insertFirstName.getText().trim();
            int age = Integer.parseInt(insertAge.getText().trim());
            String type = insertType.getValue() != null ? insertType.getValue().trim() : "";
            String email = insertEmail.getText().trim();
            String password = insertPassword.getText().trim();
            
            borrowerDAO.createBorrower(lastName, firstName, age, type, email, password);
            
            // Refresh table and clear fields
            refreshTable();
            clearFields();
            
            showAlert("Success", "Borrower account created successfully!");
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid age (numbers only).");
        } catch (SQLException e) {
            System.err.println("Error adding borrower: " + e.getMessage());
            showAlert("Error", "Failed to add borrower: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditUser() {
        Borrower selectedBorrower = borrowerList.getSelectionModel().getSelectedItem();
        
        if (selectedBorrower == null) {
            showAlert("Warning", "Please select a borrower to edit.");
            return;
        }
        
        try {
            // Validate input fields
            if (!validateInput()) {
                return;
            }
            
            // Update borrower information
            String borrowerId = selectedBorrower.getId();
            borrowerDAO.updateBorrower(borrowerId, "last_name", insertLastName.getText().trim());
            borrowerDAO.updateBorrower(borrowerId, "first_name", insertFirstName.getText().trim());
            borrowerDAO.updateBorrower(borrowerId, "age", insertAge.getText().trim());
            borrowerDAO.updateBorrower(borrowerId, "type", insertType.getValue() != null ? insertType.getValue().trim() : "");
            borrowerDAO.updateBorrower(borrowerId, "email", insertEmail.getText().trim());
            borrowerDAO.updateBorrower(borrowerId, "password", insertPassword.getText().trim());
            
            // Refresh table
            refreshTable();
            
            showAlert("Success", "Borrower information updated successfully!");
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid age (numbers only).");
        } catch (SQLException e) {
            System.err.println("Error updating borrower: " + e.getMessage());
            showAlert("Error", "Failed to update borrower: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteUser() {
        Borrower selectedBorrower = borrowerList.getSelectionModel().getSelectedItem();
        
        if (selectedBorrower == null) {
            showAlert("Warning", "Please select a borrower to delete.");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Borrower");
        confirmAlert.setContentText("Are you sure you want to delete borrower " + 
            selectedBorrower.getFirstName() + " " + selectedBorrower.getLastName() + "?");
        
        if (confirmAlert.showAndWait().orElse(null) != javafx.scene.control.ButtonType.OK) {
            return;
        }
        
        try {
            borrowerDAO.deleteBorrower(selectedBorrower.getId());
            
            // Refresh table and clear fields
            refreshTable();
            clearFields();
            
            showAlert("Success", "Borrower deleted successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error deleting borrower: " + e.getMessage());
            showAlert("Error", "Failed to delete borrower: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (insertLastName.getText().trim().isEmpty() ||
            insertFirstName.getText().trim().isEmpty() ||
            insertAge.getText().trim().isEmpty() ||
            insertType.getValue() == null || insertType.getValue().trim().isEmpty() ||
            insertEmail.getText().trim().isEmpty() ||
            insertPassword.getText().trim().isEmpty()) {
            
            showAlert("Warning", "Please fill in all fields.");
            return false;
        }
        
        try {
            int age = Integer.parseInt(insertAge.getText().trim());
            if (age < 0 || age > 120) {
                showAlert("Error", "Please enter a valid age between 0 and 120.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid age (numbers only).");
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Method to refresh the table data
    public void refreshTable() {
        loadBorrowerData();
    }
}
