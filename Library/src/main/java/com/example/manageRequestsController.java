package com.example;

import com.example.utility.dao.BorrowRequestDAO;
import com.example.utility.dao.LoanDAO;
import com.example.utility.dao.BorrowerDAO;
import com.example.utility.UserSession;
import com.example.model.Request;
import com.example.model.Request.RequestStatus;
import com.example.model.Borrower;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.util.List;

public class manageRequestsController {

    @FXML
    private Button approveRequest;

    @FXML
    private Button denyRequest;

    @FXML
    private TextField displayBookID;

    @FXML
    private TextField displayBorrowerID;

    @FXML
    private TextField displayBorrowerName;

    @FXML
    private TextField displayRequestDate;

    @FXML
    private TextField displayStatus;

    @FXML
    private TextField displayRequestID;

    @FXML
    private TableColumn<Request, String> id;

    @FXML
    private TableColumn<Request, String> requestDate;

    @FXML
    private TableView<Request> requestList;

    @FXML
    private TableColumn<Request, String> status;

    @FXML
    private TableColumn<Request, String> BookID;

    @FXML
    private TableColumn<Request, String> borrowerID;

    @FXML
    private TableColumn<Request, String> borrowerName;

    // DAO and data management
    private final BorrowRequestDAO borrowRequestDAO = new BorrowRequestDAO();
    private final LoanDAO loanDAO = new LoanDAO();
    private final BorrowerDAO borrowerDAO = new BorrowerDAO();
    private final ObservableList<Request> requestData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSelectionListener();
        loadRequests();
    }

    private void setupTableColumns() {
        // Bind columns to Request properties
        id.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        requestDate.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRequestDate().toString()));
        status.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        BookID.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBookId()));
        borrowerID.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBorrowerId()));
        
        // Set up borrower name column with a custom cell value factory
        borrowerName.setCellValueFactory(cellData -> {
            String borrowerId = cellData.getValue().getBorrowerId();
            try {
                Borrower borrower = borrowerDAO.getBorrower(borrowerId);
                if (borrower != null) {
                    return new SimpleStringProperty(borrower.getFirstName() + " " + borrower.getLastName());
                }
            } catch (Exception e) {
                System.err.println("Error fetching borrower name for ID: " + borrowerId);
            }
            return new SimpleStringProperty("Unknown");
        });
        
        requestList.setItems(requestData);
    }

    private void setupSelectionListener() {
        requestList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, selectedRequest) -> {
                if (selectedRequest != null) {
                    populateFields(selectedRequest);
                }
            }
        );
    }

    private void loadRequests() {
        try {
            List<Request> requests = borrowRequestDAO.getAllRequests();
            requestData.clear();
            requestData.addAll(requests);
            System.out.println("Loaded " + requests.size() + " requests");
        } catch (SQLException e) {
            showAlert("Error", "Failed to load requests: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void populateFields(Request request) {
        displayRequestID.setText(request.getId());
        displayRequestDate.setText(request.getRequestDate().toString());
        displayStatus.setText(request.getStatus().toString());
        displayBookID.setText(request.getBookId());
        displayBorrowerID.setText(request.getBorrowerId());
        
        // Fetch and display borrower name
        try {
            Borrower borrower = borrowerDAO.getBorrower(request.getBorrowerId());
            if (borrower != null) {
                displayBorrowerName.setText(borrower.getFirstName() + " " + borrower.getLastName());
            } else {
                displayBorrowerName.setText("Unknown");
            }
        } catch (Exception e) {
            System.err.println("Error fetching borrower name: " + e.getMessage());
            displayBorrowerName.setText("Error");
        }
    }

    @FXML
    void onActionApproveRequest(ActionEvent event) {
        Request selectedRequest = requestList.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert("Warning", "Please select a request to approve", AlertType.WARNING);
            return;
        }

        if (selectedRequest.getStatus() != RequestStatus.PENDING) {
            showAlert("Warning", "Only pending requests can be approved", AlertType.WARNING);
            return;
        }

        try {
            // Update request status
            borrowRequestDAO.updateRequestStatus(selectedRequest.getId(), RequestStatus.APPROVED);
            
            // Create loan automatically
            loanDAO.createLoan(
                selectedRequest.getBookId(),
                selectedRequest.getBorrowerId(),
                getCurrentLibrarianId()
            );
            
            showAlert("Success", "Request approved and loan created!", AlertType.INFORMATION);
            loadRequests();
            clearFields();
            
            // Refresh checkout status for the borrower
            refreshBorrowerCheckoutStatus(selectedRequest.getBorrowerId());
        } catch (SQLException e) {
            showAlert("Error", "Failed to process request: " + e.getMessage(), AlertType.ERROR);
        }
    }

    @FXML
    void onActionDenyRequest(ActionEvent event) {
        Request selectedRequest = requestList.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            showAlert("Warning", "Please select a request to deny", AlertType.WARNING);
            return;
        }

        if (selectedRequest.getStatus() != RequestStatus.PENDING) {
            showAlert("Warning", "Only pending requests can be denied", AlertType.WARNING);
            return;
        }

        try {
            borrowRequestDAO.updateRequestStatus(selectedRequest.getId(), RequestStatus.REJECTED);
            showAlert("Success", "Request denied successfully!", AlertType.INFORMATION);
            loadRequests();
            clearFields();
            
            // Refresh checkout status for the borrower
            refreshBorrowerCheckoutStatus(selectedRequest.getBorrowerId());
        } catch (SQLException e) {
            showAlert("Error", "Failed to deny request: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void clearFields() {
        displayRequestID.clear();
        displayRequestDate.clear();
        displayStatus.clear();
        displayBookID.clear();
        displayBorrowerID.clear();
        displayBorrowerName.clear();
    }

    private String getCurrentLibrarianId() {
        return UserSession.getInstance().getUserId();
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void refreshBorrowerCheckoutStatus(String borrowerId) {
        try {
            System.out.println("Refreshing checkout status for borrower: " + borrowerId);
            
            // Get the account controller instance and refresh its checkout status
            accountController accountCtrl = accountController.getInstance();
            if (accountCtrl != null) {
                accountCtrl.refreshCheckoutStatus();
                System.out.println("Checkout status refreshed successfully for borrower: " + borrowerId);
            } else {
                System.out.println("Account controller not initialized - status will update on next account page visit for borrower: " + borrowerId);
            }
            
        } catch (Exception e) {
            System.err.println("Error refreshing checkout status for borrower " + borrowerId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
